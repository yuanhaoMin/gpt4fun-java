package com.rua.controller;

import com.rua.ChamberUserPrincipal;
import com.rua.constant.ChamberPathConstants;
import com.rua.model.request.ChamberUserLoginRequestDto;
import com.rua.model.request.ChamberUserRegisterRequestDto;
import com.rua.service.ChamberUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rua.constant.ChamberPathConstants.CHAMBER_USER_LOGIN_PATH;
import static com.rua.constant.ChamberPathConstants.CHAMBER_USER_REGISTER_PATH;

@RequestMapping(value = ChamberPathConstants.CHAMBER_USER_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberUserController {

    private final ChamberUserService chamberUserService;

    @PostMapping(value = CHAMBER_USER_LOGIN_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberUserPrincipal login(@Valid @RequestBody final ChamberUserLoginRequestDto request) {
        return chamberUserService.login(request.username(), request.password());
    }

    @PostMapping(value = CHAMBER_USER_REGISTER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@Valid @RequestBody final ChamberUserRegisterRequestDto request) {
        chamberUserService.register(request.username(), request.password());
        return ResponseEntity.ok("User created");
    }

}