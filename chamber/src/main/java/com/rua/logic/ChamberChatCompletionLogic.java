package com.rua.logic;

import com.rua.entity.ChamberUserChatCompletion;
import com.rua.model.request.ChamberChatCompletionWithoutStreamRequestBo;
import com.rua.model.request.OpenAIChatCompletionMessage;
import com.rua.repository.ChamberUserChatCompletionRepository;
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

    private final ChamberUserChatCompletionRepository chamberUserChatCompletionRepository;

    private final OpenAIChatCompletionLogic openAIChatCompletionLogic;

    @Nonnull
    public ChamberUserChatCompletion findUserChatCompletionByUsername(final String username)
            throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(username);
        final var userChatCompletion = chamberUserChatCompletionRepository.findByUserId(user.getId());
        return userChatCompletion != null ?
                userChatCompletion :
                new ChamberUserChatCompletion();
    }

    public void resetChatHistory(final String username) throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(username);
        final var userChatCompletion = chamberUserChatCompletionRepository.findByUserId(user.getId());
        if (userChatCompletion != null) {
            userChatCompletion.setMessages("");
            chamberUserChatCompletionRepository.save(userChatCompletion);
        }
    }

    @Nonnull
    public List<OpenAIChatCompletionMessage> retrieveHistoryMessages(
            @Nonnull final ChamberUserChatCompletion userChatCompletion) {
        return parseJsonToList(userChatCompletion.getMessages(), OpenAIChatCompletionMessage.class);
    }

    public void updateChamberUserChatCompletion(@Nonnull final ChamberUserChatCompletion userChatCompletion,
                                                final List<OpenAIChatCompletionMessage> historyMessages,
                                                final ChamberChatCompletionWithoutStreamRequestBo request) {
        openAIChatCompletionLogic.shiftSystemMessageToHistoryEnd(historyMessages);
        userChatCompletion.setLastChatTime(getCurrentTimeInParis());
        userChatCompletion.setMessages(convertObjectToJson(historyMessages));
        // First time user chat
        if (userChatCompletion.getUser() == null) {
            userChatCompletion.setUser(chamberUserLogic.findByUsername(request.username()));
        }
        chamberUserChatCompletionRepository.save(userChatCompletion);
    }

    public void updateSystemMessageAndPersist(final String username, @Nonnull final String systemMessageContent)
            throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(username);
        var userChatCompletion = chamberUserChatCompletionRepository.findByUserId(user.getId());
        if (userChatCompletion == null) {
            userChatCompletion = new ChamberUserChatCompletion();
            userChatCompletion.setUser(user);
        }
        final var historyMessages = retrieveHistoryMessages(userChatCompletion);
        openAIChatCompletionLogic.updateSystemMessage(historyMessages, systemMessageContent);
        userChatCompletion.setMessages(convertObjectToJson(historyMessages));
        chamberUserChatCompletionRepository.save(userChatCompletion);
    }

}