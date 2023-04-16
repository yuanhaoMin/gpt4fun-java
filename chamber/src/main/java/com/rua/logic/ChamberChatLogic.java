package com.rua.logic;

import com.rua.entity.ChamberUserChatLog;
import com.rua.model.request.ChamberCompleteChatRequestBo;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.repository.ChamberUserChatLogRepository;
import com.rua.util.OpenAIGPT35Logic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rua.util.SharedDataUtils.*;

@Component
@RequiredArgsConstructor
public class ChamberChatLogic {

    private final ChamberUserChatLogRepository chamberUserChatLogRepository;

    private final OpenAIGPT35Logic openAIGPT35Logic;

    @Nonnull
    public ChamberUserChatLog findByUserId(final Long userId) {
        final var userChatLog = chamberUserChatLogRepository.findByUserId(userId);
        return userChatLog != null ? userChatLog : new ChamberUserChatLog();
    }

    public void resetChatHistory(final Long userId) {
        final var userChatLog = chamberUserChatLogRepository.findByUserId(userId);
        if (userChatLog != null) {
            userChatLog.setMessages("");
            chamberUserChatLogRepository.save(userChatLog);
        }
    }

    @Nonnull
    public List<OpenAIGPT35ChatMessage> retrieveHistoryMessages(@Nonnull final ChamberUserChatLog userChatLog) {
        final var historyMessages = convertJsonToList(userChatLog.getMessages(), OpenAIGPT35ChatMessage.class);
        return historyMessages != null ? historyMessages : new ArrayList<>();
    }

    public void updateChamberUserChatLog(@Nonnull final ChamberUserChatLog userChatLog,
                                         final List<OpenAIGPT35ChatMessage> historyMessages,
                                         final ChamberCompleteChatRequestBo request) {
        userChatLog.setUserId(request.userId());
        userChatLog.setMessages(convertObjectToJson(historyMessages));
        userChatLog.setLastChatTime(toStringNullSafe(request.lastChatTime()));
        chamberUserChatLogRepository.save(userChatLog);
    }

    public void updateSystemMessageAndPersist(final Long userId, @Nonnull final String systemMessageContent) {
        var userChatLog = chamberUserChatLogRepository.findByUserId(userId);
        if (userChatLog == null) {
            userChatLog = new ChamberUserChatLog();
        }
        final var historyMessages = retrieveHistoryMessages(userChatLog);
        openAIGPT35Logic.updateSystemMessage(historyMessages, systemMessageContent);
        userChatLog.setMessages(convertObjectToJson(historyMessages));
        chamberUserChatLogRepository.save(userChatLog);
    }

}