package com.rua.service;

import com.rua.logic.ChamberChatLogic;
import com.rua.model.request.ChamberCompleteChatRequestBo;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.request.OpenAIGPT35ChatRequestDto;
import com.rua.model.response.OpenAIGPT35ChatWithStreamData;
import com.rua.util.SharedFormatUtils;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.ChamberConstants.*;
import static com.rua.constant.OpenAIConstants.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamberChatService {

    private final OpenAIClientService openAIClientService;

    private final ChamberChatLogic chamberChatLogic;

    public String gpt35completeChat(final ChamberCompleteChatRequestBo request) {
        var userChatLog = chamberChatLogic.findUserChatLogByUserId(request.username());
        final List<OpenAIGPT35ChatMessage> messages = chamberChatLogic.retrieveHistoryMessages(userChatLog);
        // Add user message for this time prompt
        messages.add(new OpenAIGPT35ChatMessage(GPT35TURBO_USER, request.userMessage()));
        try {
            final var responseMessage = sendChatRequestToGPT35Turbo(request, messages);
            // Put in try catch to prevent the chat history from being updated if the request fails
            chamberChatLogic.updateChamberUserChatLog(userChatLog, messages, request);
            log.info(LOG_PREFIX_TIME_CHAMBER + "GPT3.5 chat completed for user: {} with temperature = {}",
                    request.username(), request.temperature());
            return responseMessage;
        } catch (FeignException.BadRequest e) {
            final var errorLog = e.toString();
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete GPT3.5 chat due to bad request: {}", errorLog);
            resetChatHistory(request.username());
            return GPT_35_CHAT_BAD_REQUEST;
        } catch (RetryableException e) {
            final var errorLog = e.toString();
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete GPT3.5 chat due to feign retryable error: {}",
                    errorLog);
            return GPT_35_CHAT_READ_TIME_OUT;
        }
    }

    public String resetChatHistory(final String username) {
        chamberChatLogic.resetChatHistory(username);
        log.info(LOG_PREFIX_TIME_CHAMBER + "Chat history reset for user: {}", username);
        return GPT_35_RESET_CHAT_HISTORY_SUCCESS;
    }

    public String updateSystemMessage(final String username, final String systemMessageContent) {
        chamberChatLogic.updateSystemMessageAndPersist(username, systemMessageContent);
        log.info(LOG_PREFIX_TIME_CHAMBER + "System message updated for user: {}", username);
        return String.format(GPT_35_SET_SYSTEM_MESSAGE_SUCCESS, systemMessageContent);
    }

    private String sendChatRequestToGPT35Turbo(final ChamberCompleteChatRequestBo request,
                                               final List<OpenAIGPT35ChatMessage> messages) {
        final var startTimeMillis = System.currentTimeMillis();
        final var openAIGPT35ChatRequest = OpenAIGPT35ChatRequestDto.builder() //
                .model(OPENAI_MODEL_GPT_35_TURBO) //
                .messages(messages) //
                .hasStream(true) //
                .temperature(request.temperature()) //
                .build();
        final List<String> collectedResponseMessage = new ArrayList<>();
        final List<OpenAIGPT35ChatWithStreamData> responseChunks = openAIClientService.gpt35ChatWithStream(
                openAIGPT35ChatRequest);
        for (final var chunk : responseChunks) {
            collectedResponseMessage.add(chunk.choices().get(0).delta().content());
        }
        final var responseContent = String.join("", collectedResponseMessage);
        // Add gpt response for next time prompt
        messages.add(new OpenAIGPT35ChatMessage(GPT35TURBO_ASSISTANT, responseContent));
        final var endTimeMillis = System.currentTimeMillis();
        final var executionTimeSeconds = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(
                endTimeMillis - startTimeMillis);
        log.info(LOG_PREFIX_TIME_CHAMBER + "GPT3.5 response received in {}s for user: {}", executionTimeSeconds,
                request.username());
        return responseContent;
    }

}