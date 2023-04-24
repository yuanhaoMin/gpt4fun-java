package com.rua.logic;

import com.rua.entity.ChamberUserChatLog;
import com.rua.model.request.ChamberCompleteChatRequestBo;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.repository.ChamberUserChatLogRepository;
import com.rua.util.OpenAIGPT35Logic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rua.util.SharedDataUtils.convertObjectToJson;
import static com.rua.util.SharedDataUtils.parseJsonToList;
import static com.rua.util.SharedFormatUtils.getCurrentTimeInParis;

@Component
@RequiredArgsConstructor
public class ChamberChatLogic {

    private final ChamberUserLogic chamberUserLogic;

    private final ChamberUserChatLogRepository chamberUserChatLogRepository;

    private final OpenAIGPT35Logic openAIGPT35Logic;

    @Nonnull
    public ChamberUserChatLog findUserChatLogByUserId(final String username) throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(username);
        final var userChatLog = chamberUserChatLogRepository.findByUserId(user.getId());
        return userChatLog != null ? userChatLog : new ChamberUserChatLog();
    }

    public void resetChatHistory(final String username) throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(username);
        final var userChatLog = chamberUserChatLogRepository.findByUserId(user.getId());
        if (userChatLog != null) {
            userChatLog.setMessages("");
            chamberUserChatLogRepository.save(userChatLog);
        }
    }

    @Nonnull
    public List<OpenAIGPT35ChatMessage> retrieveHistoryMessages(@Nonnull final ChamberUserChatLog userChatLog) {
        final var historyMessages = parseJsonToList(userChatLog.getMessages(), OpenAIGPT35ChatMessage.class);
        return historyMessages != null ? historyMessages : new ArrayList<>();
    }

    public void updateChamberUserChatLog(@Nonnull final ChamberUserChatLog userChatLog,
                                         final List<OpenAIGPT35ChatMessage> historyMessages,
                                         final ChamberCompleteChatRequestBo request) {
        openAIGPT35Logic.shiftSystemMessageToHistoryEnd(historyMessages);
        userChatLog.setLastChatTime(getCurrentTimeInParis());
        userChatLog.setMessages(convertObjectToJson(historyMessages));
        // First time user chat
        if (userChatLog.getUser() == null) {
            userChatLog.setUser(chamberUserLogic.findByUsername(request.username()));
        }
        // TODO: count request length, maybe a map of localdatetime and count
        chamberUserChatLogRepository.save(userChatLog);
    }

    public void updateSystemMessageAndPersist(final String username, @Nonnull final String systemMessageContent)
            throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(username);
        var userChatLog = chamberUserChatLogRepository.findByUserId(user.getId());
        if (userChatLog == null) {
            userChatLog = new ChamberUserChatLog();
            userChatLog.setUser(user);
        }
        final var historyMessages = retrieveHistoryMessages(userChatLog);
        openAIGPT35Logic.updateSystemMessage(historyMessages, systemMessageContent);
        userChatLog.setMessages(convertObjectToJson(historyMessages));
        chamberUserChatLogRepository.save(userChatLog);
    }

}