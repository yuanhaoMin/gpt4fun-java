package com.rua.service;

import com.rua.constant.ChamberUserAccessLevelEnum;
import com.rua.constant.OpenAIChatCompletionModelEnum;
import com.rua.logic.ChamberChatCompletionLogic;
import com.rua.logic.ChamberCompletionLogic;
import com.rua.model.request.ChamberChatCompletionWithoutStreamRequestBo;
import com.rua.model.request.OpenAIChatCompletionMessage;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
import com.rua.model.response.ChamberChatCompletionWithStreamResponseDto;
import com.rua.model.response.OpenAIChatCompletionWithStreamResponseDto;
import feign.FeignException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.ChamberConstants.*;
import static com.rua.constant.OpenAIConstants.*;
import static com.rua.util.SharedFormatUtils.createLogMessage;
import static com.rua.util.SharedJsonUtils.parseJsonToObject;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamberChatCompletionService {

    private final OpenAIClientService openAIClientService;

    private final ChamberChatCompletionLogic chamberChatCompletionLogic;

    private final ChamberCompletionLogic chamberCompletionLogic;

    // TODO: Investigate open-in-view and add transactional to necessary methods
    public Flux<ChamberChatCompletionWithStreamResponseDto> chatCompletionWithStream(final String username) {
        final var userChatCompletion = chamberChatCompletionLogic.findUserChatCompletionByUsername(username);
        final var userCompletion = chamberCompletionLogic.findUserCompletionByUsername(username);
        final var accessBitmap = userChatCompletion.getUser().getAccessBitmap();
        final var apiKey = userChatCompletion.getUser().getApiKey();
        final var model = ChamberUserAccessLevelEnum.GPT4.hasAccess(accessBitmap) ?
                OpenAIChatCompletionModelEnum.GPT4.getModelName() :
                userCompletion.getModel();
        chamberCompletionLogic.validateUserCompletion(username, userCompletion, true);
        final var messages = chamberChatCompletionLogic.retrieveHistoryMessages(userChatCompletion);
        // Add user message for this time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, userCompletion.getMessage()));
        final var openAIChatCompletionRequest = createOpenAIChatCompletionRequest(model, messages, userCompletion.getTemperature(), true);
        final List<String> collectedMessages = new ArrayList<>();
        final var startTimestamp = System.currentTimeMillis();
        return openAIClientService.chatCompletionWithStream(apiKey, openAIChatCompletionRequest) //
                .map(openAIResponse -> extractAndCollectResponseMessage(collectedMessages, openAIResponse)) //
                .filter(response -> response.content() != null)  //
                .doOnComplete(() -> {
                    final var logMessage = createLogMessage("completionWithStream", startTimestamp, username, model);
                    log.info(LOG_PREFIX_CHAMBER + logMessage);
                    // Add gpt response for next time prompt
                    messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_ASSISTANT, String.join("", collectedMessages)));
                    chamberChatCompletionLogic.updateChamberUserChatCompletion(userChatCompletion, messages, username);
                });
    }

    public String chatCompletionWithoutStream(final ChamberChatCompletionWithoutStreamRequestBo request) {
        final var username = request.username();
        final var userChatCompletion = chamberChatCompletionLogic.findUserChatCompletionByUsername(username);
        final var apiKey = userChatCompletion.getUser().getApiKey();
        final var messages = chamberChatCompletionLogic.retrieveHistoryMessages(userChatCompletion);
        // Add user message for this time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, request.userMessage()));
        final var openAIChatCompletionRequest = createOpenAIChatCompletionRequest(request.model().getModelName(), messages,
                request.temperature(), false);
        var responseContent = "";
        final var startTimestamp = System.currentTimeMillis();
        try {
            final var openAIChatCompletionResponse = openAIClientService.chatCompletionWithoutStream(apiKey,
                    openAIChatCompletionRequest);
            responseContent = openAIChatCompletionResponse.choices().get(0).message().content();
        } catch (FeignException.BadRequest e) {
            resetChatHistory(username);
            throw e;
        }
        final var logMessage = createLogMessage("completionWithStream", startTimestamp, username, request.model().getModelName());
        log.info(LOG_PREFIX_CHAMBER + logMessage);
        // Add gpt response for next time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_ASSISTANT, responseContent));
        chamberChatCompletionLogic.updateChamberUserChatCompletion(userChatCompletion, messages, username);
        return responseContent;
    }

    public String resetChatHistory(final String username) {
        chamberChatCompletionLogic.resetChatHistory(username);
        log.info(LOG_PREFIX_CHAMBER + "Reset chat history for {}", username);
        return RESET_CHAT_HISTORY_SUCCESS;
    }

    public String updateSystemMessage(final String username, final String systemMessageContent) {
        chamberChatCompletionLogic.updateSystemMessageAndPersist(username, systemMessageContent);
        log.info(LOG_PREFIX_CHAMBER + "Updated system message for {}", username);
        return String.format(SET_SYSTEM_MESSAGE_SUCCESS, systemMessageContent);
    }

    private OpenAIChatCompletionRequestDto createOpenAIChatCompletionRequest(final String model,
                                                                             final List<OpenAIChatCompletionMessage> messages,
                                                                             final double temperature, final boolean useStream) {
        return OpenAIChatCompletionRequestDto.builder() //
                .model(model) //
                .messages(messages) //
                .temperature(temperature) //
                .useStream(useStream) //
                .build();
    }

    @Nonnull
    private ChamberChatCompletionWithStreamResponseDto extractAndCollectResponseMessage(final List<String> collectedMessages,
                                                                                        final String openAIResponse) {
        if (openAIResponse.equals(END_OF_STREAM)) {
            return ChamberChatCompletionWithStreamResponseDto.builder() //
                    .content(END_OF_STREAM) //
                    .hasEnd(true) //
                    .build();
        }
        final var openAIChatCompletionWithStreamResponseDto = parseJsonToObject(openAIResponse,
                OpenAIChatCompletionWithStreamResponseDto.class);
        final var messageContent = openAIChatCompletionWithStreamResponseDto != null ?
                openAIChatCompletionWithStreamResponseDto.choices().get(0).message().content() :
                "";
        if (messageContent != null) {
            collectedMessages.add(messageContent);
        }
        return ChamberChatCompletionWithStreamResponseDto.builder() //
                .content(messageContent) //
                .hasEnd(false) //
                .build();
    }

}