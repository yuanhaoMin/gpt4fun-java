package com.rua.logic;

import com.rua.entity.ChamberUserCompletion;
import com.rua.model.request.ChamberUpdateCompletionRequestBo;
import com.rua.repository.ChamberUserCompletionRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChamberCompletionLogic {

    private final ChamberUserLogic chamberUserLogic;

    private final ChamberUserCompletionRepository chamberUserCompletionRepository;

    public void updateUserCompletionByUsername(final ChamberUpdateCompletionRequestBo request)
            throws UsernameNotFoundException {
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

}