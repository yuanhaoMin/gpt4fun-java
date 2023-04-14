package com.rua.service;

import com.rua.logic.DiscordChatLogic;
import com.rua.model.DiscordCompleteChatRequest;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.response.OpenAIGPT35ChatResponse;
import com.rua.property.DiscordProperties;
import com.rua.util.OpenAIGPT35Logic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rua.constant.DiscordConstants.*;
import static com.rua.constant.OpenAIConstants.GPT35TURBO_ASSISTANT;
import static com.rua.constant.OpenAIConstants.GPT35TURBO_USER;

@RequiredArgsConstructor
@Service
public class DiscordChatService {

    private final DiscordChatLogic discordChatLogic;

    private final DiscordProperties discordProperties;

    private final OpenAIClientService openAIClientService;

    private final OpenAIGPT35Logic openAIGPT35Logic;

    public String gpt35completeChat(final DiscordCompleteChatRequest request) {
        var guildChatLog = discordChatLogic.findByGuildId(request.guildId());
        final List<OpenAIGPT35ChatMessage> messages = discordChatLogic.retrieveHistoryMessages(guildChatLog);
        // Add user message for this time prompt
        messages.add(new OpenAIGPT35ChatMessage(GPT35TURBO_USER, request.userMessage()));
        final var gptResponse = openAIClientService.chat(messages);
        // Add gpt response for next time prompt
        messages.add(
                new OpenAIGPT35ChatMessage(GPT35TURBO_ASSISTANT, gptResponse.choices().get(0).message().content()));
        final var botResponse = generateBotResponseAndHandleTokenLimit(gptResponse, messages, request.userName());
        discordChatLogic.updateDiscordGuildChatLog(guildChatLog, messages, request);
        return botResponse;
    }

    private String generateBotResponseAndHandleTokenLimit(final OpenAIGPT35ChatResponse gptResponse,
                                                          final List<OpenAIGPT35ChatMessage> historyMessages,
                                                          String userName) {
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
            final var purgedPromptTokens = openAIGPT35Logic.limitPromptTokensByPurgingHistoryMessages(
                    estimatedNextTimePromptTokens, maxPromptTokens, historyMessages);
            botResponse.append(String.format(GPT_35_CHAT_CLEAN_HISTORY, //
                    estimatedNextTimePromptTokens, //
                    maxPromptTokens, //
                    purgedPromptTokens, //
                    maxPromptTokens));
        }
        botResponse.append(String.format(BOT_RESPONSE_PREFIX, userName));
        botResponse.append(gptResponse.choices().get(0).message().content());
        return botResponse.toString();
    }

}