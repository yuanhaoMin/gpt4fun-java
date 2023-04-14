package com.rua.logic;

import com.rua.entity.DiscordGuildChatLog;
import com.rua.model.DiscordCompleteChatRequest;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.repository.DiscordGuildChatLogRepository;
import com.rua.util.OpenAIGPT35Logic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rua.util.SharedDataUtils.convertJsonToList;
import static com.rua.util.SharedDataUtils.convertObjectToJson;

@Component
@RequiredArgsConstructor
public class DiscordChatLogic {

    private final DiscordGuildChatLogRepository discordGuildChatLogRepository;

    private final OpenAIGPT35Logic openAIGPT35Logic;

    @Nonnull
    public DiscordGuildChatLog findByGuildId(String guildId) {
        final var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        return guildChatLog != null ? guildChatLog : new DiscordGuildChatLog();
    }

    public void resetChatHistory(final String guildId) {
        final var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        if (guildChatLog != null) {
            guildChatLog.setMessages("");
            discordGuildChatLogRepository.save(guildChatLog);
        }
    }

    @Nonnull
    public List<OpenAIGPT35ChatMessage> retrieveHistoryMessages(@Nonnull final DiscordGuildChatLog guildChatLog) {
        List<OpenAIGPT35ChatMessage> historyMessages = convertJsonToList(guildChatLog.getMessages(),
                OpenAIGPT35ChatMessage.class);
        return historyMessages != null ? historyMessages : new ArrayList<>();
    }

    public void updateDiscordGuildChatLog(@Nonnull DiscordGuildChatLog guildChatLog,
                                          List<OpenAIGPT35ChatMessage> historyMessages,
                                          DiscordCompleteChatRequest request) {
        guildChatLog.setGuildId(request.guildId());
        guildChatLog.setMessages(convertObjectToJson(historyMessages));
        guildChatLog.setLastChatTime(request.lastChatTime().toString());
        guildChatLog.setLastChatUserName(request.userName());
        discordGuildChatLogRepository.save(guildChatLog);
    }

    public void updateSystemMessageAndPersist(final String guildId, @Nonnull final String systemMessageContent) {
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
        }
        final var historyMessages = retrieveHistoryMessages(guildChatLog);
        openAIGPT35Logic.updateSystemMessage(historyMessages, systemMessageContent);
        guildChatLog.setMessages(convertObjectToJson(historyMessages));
        discordGuildChatLogRepository.save(guildChatLog);
    }

}