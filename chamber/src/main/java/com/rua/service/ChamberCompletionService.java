package com.rua.service;

import com.rua.logic.ChamberCompletionLogic;
import com.rua.model.request.ChamberCompletionRequestBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.rua.constant.ChamberConstants.LOG_PREFIX_TIME_CHAMBER;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamberCompletionService {

    private final ChamberCompletionLogic chamberCompletionLogic;

    public String updateUserCompletion(final ChamberCompletionRequestBo request) {
        final var username = request.username();
        chamberCompletionLogic.updateUserCompletionByUsername(username, request);
        log.info(LOG_PREFIX_TIME_CHAMBER + "Completion data updated for {}", username);
        return "Success";
    }

}