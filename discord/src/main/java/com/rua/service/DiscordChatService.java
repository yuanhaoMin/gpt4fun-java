package com.rua.service;

import com.rua.entity.DiscordGuildChatLog;
import com.rua.model.DiscordCompleteChatRequest;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.response.OpenAIGPT35ChatResponse;
import com.rua.property.DiscordProperties;
import com.rua.repository.DiscordGuildChatLogRepository;
import com.rua.util.OpenAIGPT35Logic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.DiscordConstants.RESPONSE_EXCEED_MAX_PROMPT_TOKENS;
import static com.rua.constant.DiscordConstants.RESPONSE_EXCEED_MAX_RESPONSE_TOKENS;
import static com.rua.constant.OpenAIConstants.GPT35TURBO_ASSISTANT;
import static com.rua.constant.OpenAIConstants.GPT35TURBO_USER;
import static com.rua.util.SharedDataUtils.convertJsonToList;
import static com.rua.util.SharedDataUtils.convertObjectToJson;

@RequiredArgsConstructor
@Service
public class DiscordChatService {

    private final OpenAIClientService openAIClientService;

    private final DiscordGuildChatLogRepository discordGuildChatLogRepository;

    private final DiscordProperties discordProperties;

    private final OpenAIGPT35Logic openAIGPT35Logic;

    public String gpt35completeChat(final DiscordCompleteChatRequest discordCompleteChatRequest) {
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(discordCompleteChatRequest.guildId());
        final List<OpenAIGPT35ChatMessage> openAIGPT35ChatMessages = retrieveHistoryMessages(guildChatLog);
        openAIGPT35ChatMessages.add(
                new OpenAIGPT35ChatMessage(GPT35TURBO_USER, discordCompleteChatRequest.userMessage()));
        final var response = openAIClientService.chat(openAIGPT35ChatMessages);
        return updateChatLogAndCreateResponse(guildChatLog, openAIGPT35ChatMessages, discordCompleteChatRequest,
                response);
    }

    public void resetChatHistory(final String guildId) {
        final var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        if (guildChatLog != null) {
            guildChatLog.setMessages("");
            discordGuildChatLogRepository.save(guildChatLog);
        }
    }

    public void updateSystemMessageAndPersist(final String guildId, final String systemMessageContent) {
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        final var historyMessages = retrieveHistoryMessages(guildChatLog);
        openAIGPT35Logic.updateSystemMessage(historyMessages, systemMessageContent);
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
            guildChatLog.setGuildId(guildId);
        }
        guildChatLog.setMessages(convertObjectToJson(historyMessages));
        discordGuildChatLogRepository.save(guildChatLog);
    }

    @Nonnull
    private List<OpenAIGPT35ChatMessage> retrieveHistoryMessages(final DiscordGuildChatLog guildChatLog) {
        if (guildChatLog == null) {
            return new ArrayList<>();
        }
        List<OpenAIGPT35ChatMessage> historyOpenAIGPT35ChatMessages = null;
        historyOpenAIGPT35ChatMessages = convertJsonToList(guildChatLog.getMessages(), OpenAIGPT35ChatMessage.class);
        return historyOpenAIGPT35ChatMessages != null ? historyOpenAIGPT35ChatMessages : new ArrayList<>();
    }

    private String updateChatLogAndCreateResponse(DiscordGuildChatLog guildChatLog,
                                                  final List<OpenAIGPT35ChatMessage> historyOpenAIGPT35ChatMessages,
                                                  final DiscordCompleteChatRequest request,
                                                  final OpenAIGPT35ChatResponse response) {
        final var botResponseContent = new StringBuilder();
        // Update chat history
        final var gptResponseContent = response.choices().get(0).message().content();
        historyOpenAIGPT35ChatMessages.add(new OpenAIGPT35ChatMessage(GPT35TURBO_ASSISTANT, gptResponseContent));
        // The conversion between Chinese characters and Token is greater than 1, subtract 3 when comparing
        if (response.usage().completionTokens() >= discordProperties.maxCompletionTokens() - 3) {
            botResponseContent.append(
                            String.format(RESPONSE_EXCEED_MAX_RESPONSE_TOKENS, discordProperties.maxCompletionTokens())) //
                    .append('\n');
        }
        // Next time prompt tokens = current total tokens + next time user message tokens
        final var nextPromptTokens = response.usage().totalTokens();
        final var maxPromptTokens = discordProperties.maxPromptTokens();
        if (nextPromptTokens >= maxPromptTokens) {
            final var purgedPromptTokens = openAIGPT35Logic.limitPromptTokensByPurgingHistoryMessages(nextPromptTokens,
                    maxPromptTokens, historyOpenAIGPT35ChatMessages);
            botResponseContent.append(String.format(RESPONSE_EXCEED_MAX_PROMPT_TOKENS, //
                            nextPromptTokens, //
                            maxPromptTokens, //
                            purgedPromptTokens, //
                            maxPromptTokens)) //
                    .append('\n');
        }
        botResponseContent.append("ChatGPT answers ").append(request.userName()).append(":\n");
        // Save guild chat log
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
        }
        guildChatLog.setGuildId(request.guildId());
        guildChatLog.setMessages(convertObjectToJson(historyOpenAIGPT35ChatMessages));
        guildChatLog.setLastChatTime(request.lastChatTime().toString());
        guildChatLog.setLastChatUserName(request.userName());
        discordGuildChatLogRepository.save(guildChatLog);
        botResponseContent.append(gptResponseContent);
        return botResponseContent.toString();
    }

}