package com.rua.logic;

import com.rua.entity.DiscordGuildChatLog;
import com.rua.model.request.DiscordChatCompletionRequestBo;
import com.rua.model.request.OpenAIChatCompletionMessage;
import com.rua.repository.DiscordGuildChatLogRepository;
import com.rua.util.OpenAIChatCompletionLogic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.rua.util.SharedDataUtils.*;

@Component
@RequiredArgsConstructor
public class DiscordChatLogic {

    private final DiscordGuildChatLogRepository discordGuildChatLogRepository;

    private final OpenAIChatCompletionLogic openAIChatCompletionLogic;

    @Nonnull
    public DiscordGuildChatLog findByGuildId(final String guildId) {
        final var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        return guildChatLog != null ?
                guildChatLog :
                new DiscordGuildChatLog();
    }

    public void resetChatHistory(final String guildId) {
        final var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        if (guildChatLog != null) {
            guildChatLog.setMessages("");
            discordGuildChatLogRepository.save(guildChatLog);
        }
    }

    @Nonnull
    public List<OpenAIChatCompletionMessage> retrieveHistoryMessages(@Nonnull final DiscordGuildChatLog guildChatLog) {
        return parseJsonToList(guildChatLog.getMessages(), OpenAIChatCompletionMessage.class);
    }

    public void updateDiscordGuildChatLog(@Nonnull final DiscordGuildChatLog guildChatLog,
                                          final List<OpenAIChatCompletionMessage> historyMessages,
                                          final DiscordChatCompletionRequestBo request) {
        guildChatLog.setGuildId(request.guildId());
        guildChatLog.setMessages(convertObjectToJson(historyMessages));
        guildChatLog.setLastChatTime(toStringNullSafe(request.lastChatTime()));
        guildChatLog.setLastChatUsername(request.username());
        discordGuildChatLogRepository.save(guildChatLog);
    }

    public void updateSystemMessageAndPersist(final String guildId, @Nonnull final String systemMessageContent) {
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
        }
        final var historyMessages = retrieveHistoryMessages(guildChatLog);
        openAIChatCompletionLogic.updateSystemMessage(historyMessages, systemMessageContent);
        guildChatLog.setMessages(convertObjectToJson(historyMessages));
        discordGuildChatLogRepository.save(guildChatLog);
    }

}