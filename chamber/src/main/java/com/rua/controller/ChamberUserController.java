package com.rua.controller;

import com.rua.ChamberUserPrincipal;
import com.rua.constant.ChamberControllerConstants;
import com.rua.logic.ChamberUserLogic;
import com.rua.model.request.ChamberUserLoginRequestDto;
import com.rua.model.request.ChamberUserRegisterRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_USER_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberUserController {

    private final ChamberUserLogic chamberUserLogic;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberUserPrincipal login(@Valid @RequestBody final ChamberUserLoginRequestDto request) {
        return chamberUserLogic.authenticateUser(request.username(), request.password());
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@Valid @RequestBody final ChamberUserRegisterRequestDto request) {
        chamberUserLogic.createUser(request.username(), request.password());
        return ResponseEntity.ok("User created");
    }

}