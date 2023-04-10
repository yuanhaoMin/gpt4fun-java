package com.rua.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rua.entity.DiscordGuildChatLog;
import com.rua.model.DiscordCompleteChatRequest;
import com.rua.model.request.Message;
import com.rua.model.response.ChatGPTResponse;
import com.rua.property.DiscordProperties;
import com.rua.repository.DiscordGuildChatLogRepository;
import com.rua.util.SharedGPT35Logic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.DiscordConstants.RESPONSE_EXCEED_MAX_PROMPT_TOKENS;
import static com.rua.constant.DiscordConstants.RESPONSE_EXCEED_MAX_RESPONSE_TOKENS;
import static com.rua.util.SharedDataUtils.isNullOrEmpty;

@RequiredArgsConstructor
@Service
public class DiscordChatService {

    private final OpenAIClientService openAIClientService;

    private final DiscordGuildChatLogRepository discordGuildChatLogRepository;

    private final DiscordProperties discordProperties;

    private final SharedGPT35Logic sharedGPT35Logic;

    public String gpt35completeChat(final DiscordCompleteChatRequest discordCompleteChatRequest) {
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(discordCompleteChatRequest.guildId());
        final List<Message> messages = retrieveHistoryMessages(guildChatLog);
        messages.add(new Message("user", discordCompleteChatRequest.userMessage()));
        final var response = openAIClientService.chat(messages);
        return updateChatLogAndCreateResponse(guildChatLog, messages, discordCompleteChatRequest, response);
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
        sharedGPT35Logic.updateSystemMessage(historyMessages, systemMessageContent);
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
            guildChatLog.setGuildId(guildId);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            guildChatLog.setMessages(mapper.writeValueAsString(historyMessages));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        discordGuildChatLogRepository.save(guildChatLog);
    }

    @Nonnull
    private List<Message> retrieveHistoryMessages(final DiscordGuildChatLog guildChatLog) {
        if (guildChatLog == null) {
            return new ArrayList<>();
        }
        // TODO change to util
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<Message> historyMessages = null;
        try {
            if (isNullOrEmpty(guildChatLog.getMessages())) {
                return new ArrayList<>();
            }
            historyMessages = mapper.readValue(guildChatLog.getMessages(),
                    new TypeReference<List<Message>>() {
                    });
            // TODO exception handling
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return historyMessages != null ? historyMessages : new ArrayList<>();
    }

    private String updateChatLogAndCreateResponse(DiscordGuildChatLog guildChatLog,
                                                  final List<Message> historyMessages,
                                                  final DiscordCompleteChatRequest request,
                                                  final ChatGPTResponse response) {
        final var botResponseContent = new StringBuilder();
        // Update chat history
        final var gptResponseContent = response.choices().get(0).message().content();
        historyMessages.add(new Message("assistant", gptResponseContent));
        // The conversion between Chinese characters and Token is greater than 1, subtract 3 when comparing
        if (response.usage().getCompletionTokens() >= discordProperties.maxCompletionTokens() - 3) {
            botResponseContent.append(
                            String.format(RESPONSE_EXCEED_MAX_RESPONSE_TOKENS, discordProperties.maxCompletionTokens())) //
                    .append('\n');
        }
        // Next time prompt tokens = current total tokens + next time user message tokens
        final var nextPromptTokens = response.usage().getTotalTokens();
        final var maxPromptTokens = discordProperties.maxPromptTokens();
        if (nextPromptTokens >= maxPromptTokens) {
            final var purgedPromptTokens = sharedGPT35Logic.limitPromptTokensByPurgingHistoryMessages(nextPromptTokens,
                    maxPromptTokens, historyMessages);
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
        ObjectMapper mapper = new ObjectMapper();
        try {
            guildChatLog.setMessages(mapper.writeValueAsString(historyMessages));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        guildChatLog.setLastChatTime(request.lastChatTime().toString());
        guildChatLog.setLastChatUserName(request.userName());
        discordGuildChatLogRepository.save(guildChatLog);
        botResponseContent.append(gptResponseContent);
        return botResponseContent.toString();
    }

}