package com.rua.service;

import com.rua.ChamberUserPrincipal;
import com.rua.logic.ChamberUserLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.rua.constant.ChamberConstants.LOG_PREFIX_TIME_CHAMBER;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamberUserService {

    private final ChamberUserLogic chamberUserLogic;

    public ChamberUserPrincipal login(String username, String password) {
        final var authenticatedUser = chamberUserLogic.authenticateUser(username, password);
        log.info(LOG_PREFIX_TIME_CHAMBER + "{} logged in", username);
        return authenticatedUser;
    }

    public void register(String username, String password) {
        chamberUserLogic.createUser(username, password);
        log.info(LOG_PREFIX_TIME_CHAMBER + "{} registered", username);
    }

}