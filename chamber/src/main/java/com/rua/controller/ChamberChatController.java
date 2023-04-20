package com.rua.controller;

import com.rua.constant.ChamberControllerConstants;
import com.rua.model.request.ChamberCompleteChatRequestBo;
import com.rua.model.request.ChamberCompleteChatRequestDto;
import com.rua.model.request.ChamberUpdateSystemMessageRequestDto;
import com.rua.model.response.ChamberCompleteChatResponseDto;
import com.rua.model.response.ChamberResetChatHistoryResponseDto;
import com.rua.model.response.ChamberUpdateSystemMessageResponseDto;
import com.rua.service.ChamberChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_CHAT_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberChatController {

    private final ChamberChatService chamberChatService;

    @PostMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberCompleteChatResponseDto completeChat(final Authentication authentication,
                                                       @Valid @RequestBody final ChamberCompleteChatRequestDto requestDto) {
        final var requestBo = ChamberCompleteChatRequestBo.builder() //
                .temperature(requestDto.temperature()) //
                .username(authentication.getName()) //
                .userMessage(requestDto.userMessage()) //
                .build();
        final var responseMessage = chamberChatService.gpt35completeChat(requestBo);
        return ChamberCompleteChatResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @DeleteMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberResetChatHistoryResponseDto deleteHistory(final Authentication authentication) {
        final var responseMessage = chamberChatService.resetChatHistory(authentication.getName());
        return ChamberResetChatHistoryResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @PutMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberUpdateSystemMessageResponseDto updateSystemMessage(final Authentication authentication,
                                                                     @RequestBody final ChamberUpdateSystemMessageRequestDto requestDto) {
        final var responseMessage = chamberChatService.updateSystemMessage(authentication.getName(),
                requestDto.systemMessage());
        return ChamberUpdateSystemMessageResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

}