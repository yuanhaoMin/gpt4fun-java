package com.rua.logic;

import com.rua.constant.OpenAIChatCompletionModelEnum;
import com.rua.constant.OpenAICompletionModelEnum;
import com.rua.constant.OpenAIModelInfo;
import com.rua.entity.ChamberUserCompletion;
import com.rua.model.request.ChamberUpdateCompletionRequestBo;
import com.rua.repository.ChamberUserCompletionRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.rua.constant.ChamberConstants.*;
import static com.rua.util.SharedDataUtils.isNullOrEmpty;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChamberCompletionLogic {

    private final ChamberUserLogic chamberUserLogic;

    private final ChamberUserCompletionRepository chamberUserCompletionRepository;

    public void updateUserCompletionByUsername(final ChamberUpdateCompletionRequestBo request) throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(request.username());
        var userCompletion = chamberUserCompletionRepository.findByUserId(user.getId());
        if (userCompletion == null) {
            userCompletion = new ChamberUserCompletion();
        }
        userCompletion.setMessage(request.message());
        userCompletion.setModel(request.model());
        userCompletion.setTemperature(request.temperature());
        userCompletion.setUser(user);
        chamberUserCompletionRepository.save(userCompletion);
    }

    @Nonnull
    public ChamberUserCompletion findUserCompletionByUsername(final String username) throws UsernameNotFoundException {
        final var user = chamberUserLogic.findByUsername(username);
        final var userCompletion = chamberUserCompletionRepository.findByUserId(user.getId());
        return userCompletion != null ?
                userCompletion :
                new ChamberUserCompletion();
    }

    public void validateUserCompletion(final String username, final ChamberUserCompletion userCompletion, boolean isChatCompletion) {
        final var modelName = userCompletion.getModel();
        if (isNullOrEmpty(modelName)) {
            throw new IllegalArgumentException(String.format(ERROR_CAUSE_EMPTY_MODEL, username));
        }
        try {
            if (isChatCompletion) {
                OpenAIModelInfo.get(modelName, OpenAIChatCompletionModelEnum.class);
            } else {
                OpenAIModelInfo.get(modelName, OpenAICompletionModelEnum.class);
            }
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(ERROR_CAUSE_INVALID_MODEL, modelName, username));
        }
        if (isNullOrEmpty(userCompletion.getMessage())) {
            throw new IllegalArgumentException(String.format(ERROR_CAUSE_EMPTY_MESSAGE, username));
        }
    }

}