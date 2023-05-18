package com.rua.service;

import com.rua.constant.OpenAIChatCompletionModelEnum;
import com.rua.logic.DiscordChatLogic;
import com.rua.model.request.DiscordChatCompletionRequestBo;
import com.rua.model.request.OpenAIChatCompletionMessage;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
import com.rua.model.response.OpenAIChatCompletionWithoutStreamResponseDto;
import com.rua.property.DiscordProperties;
import com.rua.util.OpenAIChatCompletionLogic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rua.constant.DiscordConstants.*;
import static com.rua.constant.OpenAIConstants.CHAT_COMPLETION_ROLE_ASSISTANT;
import static com.rua.constant.OpenAIConstants.CHAT_COMPLETION_ROLE_USER;

@RequiredArgsConstructor
@Service
public class DiscordChatService {

    private final OpenAIClientService openAIClientService;

    private final DiscordChatLogic discordChatLogic;

    private final OpenAIChatCompletionLogic openAIChatCompletionLogic;

    private final DiscordProperties discordProperties;

    public String gpt35ChatCompletion(final DiscordChatCompletionRequestBo request) {
        var guildChatLog = discordChatLogic.findByGuildId(request.guildId());
        final List<OpenAIChatCompletionMessage> messages = discordChatLogic.retrieveHistoryMessages(guildChatLog);
        // Add user message for this time prompt
        messages.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, request.userMessage()));
        final var openAIGPT35ChatRequest = OpenAIChatCompletionRequestDto.builder() //
                .model(OpenAIChatCompletionModelEnum.GPT35.getModelName()) //
                .messages(messages) //
                .useStream(false) //
                .temperature(0.4) //
                .build();
        final var gptResponse = openAIClientService.chatCompletionWithoutStream("", openAIGPT35ChatRequest);
        // Add gpt response for next time prompt
        messages.add(
                new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_ASSISTANT,
                        gptResponse.choices().get(0).message().content()));
        openAIChatCompletionLogic.shiftSystemMessageToHistoryEnd(messages);
        final var botResponse = generateBotResponseAndHandleTokenLimit(gptResponse, messages, request.username());
        discordChatLogic.updateDiscordGuildChatLog(guildChatLog, messages, request);
        return botResponse;
    }

    private String generateBotResponseAndHandleTokenLimit(
            final OpenAIChatCompletionWithoutStreamResponseDto gptResponse,
            @Nonnull final List<OpenAIChatCompletionMessage> historyMessages,
            final String username) {
        final var botResponse = new StringBuilder();
        // Next time prompt tokens = current total tokens + estimated next time prompt tokens
        final var estimatedNextTimePromptTokens = gptResponse.usage()
                .totalTokens() + discordProperties.estimatedPromptLength();
        if (estimatedNextTimePromptTokens == discordProperties.maxTotalTokens()) {
            botResponse.append(String.format(GPT_35_CHAT_TRUNCATE_RESPONSE, discordProperties.maxPromptTokens()));
        }
        final var maxPromptTokens = discordProperties.maxPromptTokens();
        // Need to purge history messages if next time prompt tokens exceed max prompt tokens
        if (estimatedNextTimePromptTokens >= maxPromptTokens) {
            botResponse.append(String.format(GPT_35_CHAT_TOKEN_LIMIT, //
                    estimatedNextTimePromptTokens, //
                    discordProperties.maxTotalTokens()));
            final var purgedPromptTokens = openAIChatCompletionLogic.limitPromptTokensByPurgingHistoryMessages(
                    estimatedNextTimePromptTokens, maxPromptTokens, historyMessages);
            botResponse.append(String.format(GPT_35_CHAT_CLEAN_HISTORY, //
                    estimatedNextTimePromptTokens, //
                    maxPromptTokens, //
                    purgedPromptTokens, //
                    maxPromptTokens));
        }
        botResponse.append(String.format(BOT_RESPONSE_PREFIX, username));
        botResponse.append(gptResponse.choices().get(0).message().content());
        return botResponse.toString();
    }

}