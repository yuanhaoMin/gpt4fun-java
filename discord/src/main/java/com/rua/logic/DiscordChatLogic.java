package com.rua.logic;

import com.rua.entity.DiscordGuildChatLog;
import com.rua.model.request.DiscordCompleteChatRequestBo;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.repository.DiscordGuildChatLogRepository;
import com.rua.util.OpenAIGPT35Logic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rua.util.SharedDataUtils.*;

@Component
@RequiredArgsConstructor
public class DiscordChatLogic {

    private final DiscordGuildChatLogRepository discordGuildChatLogRepository;

    private final OpenAIGPT35Logic openAIGPT35Logic;

    @Nonnull
    public DiscordGuildChatLog findByGuildId(final String guildId) {
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
        final var historyMessages = parseJsonToList(guildChatLog.getMessages(), OpenAIGPT35ChatMessage.class);
        return historyMessages != null ? historyMessages : new ArrayList<>();
    }

    public void updateDiscordGuildChatLog(@Nonnull final DiscordGuildChatLog guildChatLog,
                                          final List<OpenAIGPT35ChatMessage> historyMessages,
                                          final DiscordCompleteChatRequestBo request) {
        guildChatLog.setGuildId(request.guildId());
        guildChatLog.setMessages(convertObjectToJson(historyMessages));
        guildChatLog.setLastChatTime(toStringNullSafe(request.lastChatTime()));
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