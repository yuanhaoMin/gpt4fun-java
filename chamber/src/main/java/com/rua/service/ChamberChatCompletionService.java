package com.rua.service;

import com.rua.logic.ChamberChatCompletionLogic;
import com.rua.model.request.ChamberChatCompletionRequestBo;
import com.rua.model.request.OpenAIChatCompletionMessage;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
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
import static com.rua.constant.OpenAIConstants.CHAT_COMPLETION_ROLE_ASSISTANT;
import static com.rua.constant.OpenAIConstants.CHAT_COMPLETION_ROLE_USER;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamberChatCompletionService {

    private final OpenAIClientService openAIClientService;

    private final ChamberChatCompletionLogic chamberChatCompletionLogic;

    public String chatCompletion(final ChamberChatCompletionRequestBo request) {
        var userChatLog = chamberChatCompletionLogic.findUserChatLogByUserId(request.username());
        final List<OpenAIChatCompletionMessage> messages = chamberChatCompletionLogic.retrieveHistoryMessages(
                userChatLog);
        // Add user message for this time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, request.userMessage()));
        try {
            final var responseMessage = sendChatCompletionRequest(request, messages);
            // Put in try catch to prevent the chat history from being updated if the request fails
            chamberChatCompletionLogic.updateChamberUserChatLog(userChatLog, messages, request);
            log.info(LOG_PREFIX_TIME_CHAMBER + "{} chat completed for user: {} with temperature = {}", request.model(),
                    request.username(), request.temperature());
            return responseMessage;
        } catch (FeignException.BadRequest e) {
            final var errorLog = e.toString();
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete {} chat due to bad request: {}", request.model(),
                    errorLog);
            resetChatHistory(request.username());
            return CHAT_COMPLETION_BAD_REQUEST;
        } catch (RetryableException e) {
            final var errorLog = e.toString();
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete {} chat due to feign retryable error: {}",
                    request.model(),
                    errorLog);
            return CHAT_COMPLETION_READ_TIME_OUT;
        }
    }

    public String resetChatHistory(final String username) {
        chamberChatCompletionLogic.resetChatHistory(username);
        log.info(LOG_PREFIX_TIME_CHAMBER + "Chat history reset for user: {}", username);
        return RESET_CHAT_HISTORY_SUCCESS;
    }

    public String updateSystemMessage(final String username, final String systemMessageContent) {
        chamberChatCompletionLogic.updateSystemMessageAndPersist(username, systemMessageContent);
        log.info(LOG_PREFIX_TIME_CHAMBER + "System message updated for user: {}", username);
        return String.format(SET_SYSTEM_MESSAGE_SUCCESS, systemMessageContent);
    }

    private String sendChatCompletionRequest(final ChamberChatCompletionRequestBo request,
                                             final List<OpenAIChatCompletionMessage> messages) {
        final var startTimeMillis = System.currentTimeMillis();
        final var openAIChatCompletionRequest = OpenAIChatCompletionRequestDto.builder() //
                .model(request.model()) //
                .messages(messages) //
                .hasStream(true) //
                .temperature(request.temperature()) //
                .build();
        final List<String> collectedResponseMessage = new ArrayList<>();
        final List<OpenAIGPT35ChatWithStreamData> responseChunks = openAIClientService.gpt35ChatWithStream(
                openAIChatCompletionRequest);
        for (final var chunk : responseChunks) {
            collectedResponseMessage.add(chunk.choices().get(0).delta().content());
        }
        final var responseContent = String.join("", collectedResponseMessage);
        // Add gpt response for next time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_ASSISTANT, responseContent));
        final var endTimeMillis = System.currentTimeMillis();
        final var executionTimeSeconds = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(
                endTimeMillis - startTimeMillis);
        log.info(LOG_PREFIX_TIME_CHAMBER + "{} response received in {}s for user: {}", request.model(),
                executionTimeSeconds,
                request.username());
        return responseContent;
    }

}