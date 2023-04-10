package com.rua.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rua.entity.DiscordGuildChatLog;
import com.rua.model.DiscordCompleteChatRequest;
import com.rua.model.request.Message;
import com.rua.model.response.ChatGPTResponse;
import com.rua.property.DiscordProperties;
import com.rua.repository.DiscordGuildChatLogRepository;
import com.rua.util.SharedGPT35Logic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.DiscordConstants.RESPONSE_EXCEED_MAX_PROMPT_TOKENS;
import static com.rua.constant.DiscordConstants.RESPONSE_EXCEED_MAX_RESPONSE_TOKENS;

@RequiredArgsConstructor
@Service
public class DiscordChatService {

    private final OpenAIClientService openAIClientService;

    private final DiscordGuildChatLogRepository discordGuildChatLogRepository;

    private final DiscordProperties discordProperties;

    private final SharedGPT35Logic sharedGPT35Logic;

    public String gpt35completeChat(final DiscordCompleteChatRequest discordCompleteChatRequest) {
        final var gson = new Gson();
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(discordCompleteChatRequest.guildId());
        final List<Message> messages = retrieveHistoryMessages(gson, guildChatLog);
        messages.add(new Message("user", discordCompleteChatRequest.userMessage()));
        final var response = openAIClientService.chat(messages);
        return updateChatLogAndCreateResponse(gson, guildChatLog, messages, discordCompleteChatRequest, response);
    }

    public void resetChatHistory(final String guildId) {
        final var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        if (guildChatLog != null) {
            guildChatLog.setMessages("");
            discordGuildChatLogRepository.save(guildChatLog);
        }
    }

    public void updateSystemMessageAndPersist(final String guildId, final String systemMessageContent) {
        final var gson = new Gson();
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        final var historyMessages = retrieveHistoryMessages(gson, guildChatLog);
        sharedGPT35Logic.updateSystemMessage(historyMessages, systemMessageContent);
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
            guildChatLog.setGuildId(guildId);
        }
        guildChatLog.setMessages(gson.toJson(historyMessages));
        discordGuildChatLogRepository.save(guildChatLog);
    }

    private List<Message> retrieveHistoryMessages(final Gson gson, final DiscordGuildChatLog guildChatLog) {
        if (guildChatLog == null) {
            return new ArrayList<>();
        }
        List<Message> historyMessages = gson.fromJson(guildChatLog.getMessages(), new TypeToken<ArrayList<Message>>() {
        }.getType());
        return historyMessages != null ? historyMessages : new ArrayList<>();
    }

    private String updateChatLogAndCreateResponse(final Gson gson, DiscordGuildChatLog guildChatLog,
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
        guildChatLog.setMessages(gson.toJson(historyMessages));
        guildChatLog.setLastChatTime(request.lastChatTime());
        guildChatLog.setLastChatUserName(request.userName());
        discordGuildChatLogRepository.save(guildChatLog);
        botResponseContent.append(gptResponseContent);
        return botResponseContent.toString();
    }

}