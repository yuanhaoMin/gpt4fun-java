package com.rua.logic;

import com.rua.entity.ChamberUserChatLog;
import com.rua.model.request.ChamberChatCompletionRequestBo;
import com.rua.model.request.OpenAIChatCompletionMessage;
import com.rua.repository.ChamberUserChatLogRepository;
import com.rua.util.OpenAIChatCompletionLogic;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.rua.util.SharedDataUtils.convertObjectToJson;
import static com.rua.util.SharedDataUtils.parseJsonToList;
import static com.rua.util.SharedFormatUtils.getCurrentTimeInParis;

@Component
@RequiredArgsConstructor
public class ChamberChatCompletionLogic {

    private final ChamberUserLogic chamberUserLogic;

    private final ChamberUserChatLogRepository chamberUserChatLogRepository;

    private final OpenAIChatCompletionLogic openAIChatCompletionLogic;

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
    public List<OpenAIChatCompletionMessage> retrieveHistoryMessages(@Nonnull final ChamberUserChatLog userChatLog) {
        return parseJsonToList(userChatLog.getMessages(), OpenAIChatCompletionMessage.class);
    }

    public void updateChamberUserChatLog(@Nonnull final ChamberUserChatLog userChatLog,
                                         final List<OpenAIChatCompletionMessage> historyMessages,
                                         final ChamberChatCompletionRequestBo request) {
        openAIChatCompletionLogic.shiftSystemMessageToHistoryEnd(historyMessages);
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
        openAIChatCompletionLogic.updateSystemMessage(historyMessages, systemMessageContent);
        userChatLog.setMessages(convertObjectToJson(historyMessages));
        chamberUserChatLogRepository.save(userChatLog);
    }

}