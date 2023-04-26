package com.rua.controller;

import com.rua.model.request.ChamberCompletionRequestBo;
import com.rua.model.request.ChamberCompletionRequestDto;
import com.rua.model.response.ChamberCompletionResponseDto;
import com.rua.service.ChamberCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rua.constant.ChamberPathConstants.CHAMBER_COMPLETION_CONTROLLER_PATH;
import static com.rua.constant.ChamberPathConstants.CHAMBER_COMPLETION_UPDATE_COMPLETION_DATA_PATH;

@RequestMapping(value = CHAMBER_COMPLETION_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberCompletionController {

    private final ChamberCompletionService chamberCompletionService;

    @PutMapping(value = CHAMBER_COMPLETION_UPDATE_COMPLETION_DATA_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberCompletionResponseDto updateCompletion(final Authentication authentication,
                                                         @Valid @RequestBody final ChamberCompletionRequestDto requestDto) {
        final var requestBo = ChamberCompletionRequestBo.builder() //
                .model(requestDto.model()) //
                .message(requestDto.message()) //
                .temperature(requestDto.temperature()) //
                .username(authentication.getName()) //
                .build();
        final var responseMessage = chamberCompletionService.updateUserCompletion(requestBo);
        return ChamberCompletionResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

}