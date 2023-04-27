package com.rua.service;

import com.rua.entity.ChamberUserChatCompletion;
import com.rua.logic.ChamberChatCompletionLogic;
import com.rua.logic.ChamberCompletionLogic;
import com.rua.model.request.ChamberChatCompletionWithoutStreamRequestBo;
import com.rua.model.request.OpenAIChatCompletionMessage;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
import com.rua.model.response.ChamberChatCompletionWithStreamResponseDto;
import com.rua.model.response.OpenAIChatCompletionWithStreamResponseDto;
import com.rua.util.SharedFormatUtils;
import feign.FeignException;
import feign.RetryableException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.ChamberConstants.*;
import static com.rua.constant.OpenAIConstants.*;
import static com.rua.util.SharedDataUtils.isNullOrEmpty;
import static com.rua.util.SharedDataUtils.parseJsonToObject;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamberChatCompletionService {

    private final OpenAIClientService openAIClientService;

    private final ChamberChatCompletionLogic chamberChatCompletionLogic;

    private final ChamberCompletionLogic chamberCompletionLogic;

    // TODO: refactor all methods, move some methods to ChamberChatCompletionLogic
    public Flux<ChamberChatCompletionWithStreamResponseDto> chatCompletionWithStream(final String username) {
        final var userChatCompletion = chamberChatCompletionLogic.findUserChatCompletionByUsername(username);
        final var userCompletion = chamberCompletionLogic.findUserCompletionByUsername(username);
        final var model = userCompletion.getModel();
        final var userMessage = userCompletion.getMessage();
        if (isNullOrEmpty(model)) {
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete chat for {} due to empty model", username);
            final var errorResponse = ChamberChatCompletionWithStreamResponseDto.builder() //
                    .content("No model found") //
                    .hasEnd(true) //
                    .build();
            return Flux.just(errorResponse);
        } else if (isNullOrEmpty(userMessage)) {
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete chat between {} and {} due to empty message",
                    username, model);
            final var errorResponse = ChamberChatCompletionWithStreamResponseDto.builder() //
                    .content("No message found") //
                    .hasEnd(true) //
                    .build();
            return Flux.just(errorResponse);
        }
        final var chatCompletionRequest = ChamberChatCompletionWithoutStreamRequestBo.builder() //
                .model(model) //
                .temperature(userCompletion.getTemperature()) //
                .username(username) //
                .userMessage(userMessage) //
                .build();
        final var messages = chamberChatCompletionLogic.retrieveHistoryMessages(userChatCompletion);
        // Add user message for this time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, userMessage));
        final var openAIChatCompletionRequest = createOpenAIChatCompletionRequest(chatCompletionRequest, messages,
                true);
        final List<String> collectedMessages = new ArrayList<>();
        final var startTimeMillis = System.currentTimeMillis();
        try {
            return openAIClientService.chatCompletionWithStream(openAIChatCompletionRequest) //
                    .map(openAIResponse -> extractAndCollectResponseMessage(collectedMessages, openAIResponse)) //
                    .filter(response -> response.content() != null)  //
                    .doOnComplete(() -> processChatCompletionResponse(startTimeMillis, messages,
                            String.join("", collectedMessages), userChatCompletion, chatCompletionRequest));
        } catch (WebClientResponseException.BadRequest e) {
            return Flux.just(ChamberChatCompletionWithStreamResponseDto.builder() //
                    .content(CHAT_COMPLETION_BAD_REQUEST) //
                    .hasEnd(true) //
                    .build());
        }
    }

    public String chatCompletionWithoutStream(final ChamberChatCompletionWithoutStreamRequestBo request) {
        final var username = request.username();
        final var model = request.model();
        final var userChatCompletion = chamberChatCompletionLogic.findUserChatCompletionByUsername(username);
        final var messages = chamberChatCompletionLogic.retrieveHistoryMessages(userChatCompletion);
        // Add user message for this time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, request.userMessage()));
        try {
            return sendChatCompletionRequestWithoutStream(userChatCompletion, request, messages);
        } catch (FeignException.BadRequest e) {
            final var errorLog = e.toString();
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete chat between {} and {} due to bad request: {}",
                    username, model, errorLog);
            resetChatHistory(username);
            return CHAT_COMPLETION_BAD_REQUEST;
        } catch (RetryableException e) {
            final var errorLog = e.toString();
            log.error(
                    LOG_PREFIX_TIME_CHAMBER + "Unable to complete chat between {} and {} due to feign retryable error: {}",
                    username, model, errorLog);
            return CHAT_COMPLETION_READ_TIME_OUT;
        }
    }

    public String resetChatHistory(final String username) {
        chamberChatCompletionLogic.resetChatHistory(username);
        log.info(LOG_PREFIX_TIME_CHAMBER + "Chat history reset for {}", username);
        return RESET_CHAT_HISTORY_SUCCESS;
    }

    public String updateSystemMessage(final String username, final String systemMessageContent) {
        chamberChatCompletionLogic.updateSystemMessageAndPersist(username, systemMessageContent);
        log.info(LOG_PREFIX_TIME_CHAMBER + "System message updated for {}", username);
        return String.format(SET_SYSTEM_MESSAGE_SUCCESS, systemMessageContent);
    }

    private OpenAIChatCompletionRequestDto createOpenAIChatCompletionRequest(
            final ChamberChatCompletionWithoutStreamRequestBo request, final List<OpenAIChatCompletionMessage> messages,
            final boolean useStream) {
        return OpenAIChatCompletionRequestDto.builder().model(request.model()).messages(messages).useStream(useStream)
                .temperature(request.temperature()).build();
    }

    @Nonnull
    private ChamberChatCompletionWithStreamResponseDto extractAndCollectResponseMessage(
            final List<String> collectedMessages, final String openAIResponse) {
        if (openAIResponse.equals(END_OF_CHAT_COMPLETION_STREAM)) {
            return ChamberChatCompletionWithStreamResponseDto.builder() //
                    .content(END_OF_CHAT_COMPLETION_STREAM) //
                    .hasEnd(true) //
                    .build();
        }
        final var openAIChatCompletionWithStreamResponseDto = parseJsonToObject(openAIResponse,
                OpenAIChatCompletionWithStreamResponseDto.class);
        final var messageContent = openAIChatCompletionWithStreamResponseDto != null ? openAIChatCompletionWithStreamResponseDto.choices()
                .get(0).message().content() : "";
        if (messageContent != null) {
            collectedMessages.add(messageContent);
        }
        return ChamberChatCompletionWithStreamResponseDto.builder() //
                .content(messageContent) //
                .hasEnd(false) //
                .build();
    }

    private String sendChatCompletionRequestWithoutStream(final ChamberUserChatCompletion userChatCompletion,
                                                          final ChamberChatCompletionWithoutStreamRequestBo request,
                                                          final List<OpenAIChatCompletionMessage> messages) {
        final var startTimeMillis = System.currentTimeMillis();
        final var openAIChatCompletionRequest = createOpenAIChatCompletionRequest(request, messages, false);
        var responseContent = "";
        final var openAIChatCompletionResponse = openAIClientService.chatCompletionWithoutStream(
                openAIChatCompletionRequest);
        responseContent = openAIChatCompletionResponse.choices().get(0).message().content();
        processChatCompletionResponse(startTimeMillis, messages, responseContent, userChatCompletion, request);
        return responseContent;
    }

    private void processChatCompletionResponse(final long startTimeMillis,
                                               final List<OpenAIChatCompletionMessage> messages,
                                               final String responseContent,
                                               final ChamberUserChatCompletion userChatCompletion,
                                               final ChamberChatCompletionWithoutStreamRequestBo request) {
        final var username = request.username();
        final var model = request.model();
        final var temperature = request.temperature();
        // Add gpt response for next time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_ASSISTANT, responseContent));
        final var endTimeMillis = System.currentTimeMillis();
        final var executionTimeSeconds = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(
                endTimeMillis - startTimeMillis);
        log.info(LOG_PREFIX_TIME_CHAMBER + "Response received within {}s for chat between {} and {}",
                executionTimeSeconds, username, model);
        chamberChatCompletionLogic.updateChamberUserChatCompletion(userChatCompletion, messages, request);
        log.info(LOG_PREFIX_TIME_CHAMBER + "Chat completed between {} and {} with temperature = {}", username, model,
                temperature);
    }

}