package com.rua.controller;

import com.rua.constant.ChamberControllerConstants;
import com.rua.model.request.ChamberCompleteChatRequestBo;
import com.rua.model.request.ChamberCompleteChatRequestDto;
import com.rua.model.request.ChamberUpdateSystemMessageRequestDto;
import com.rua.model.response.ChamberCompleteChatResponseDto;
import com.rua.model.response.ChamberResetChatHistoryResponseDto;
import com.rua.model.response.ChamberUpdateSystemMessageResponseDto;
import com.rua.service.ChamberChatService;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.rua.constant.ChamberConstants.*;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_CHAT_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChamberChatController {

    private final ChamberChatService chamberChatService;

    // TODO validation, Exceeding limit error handling instead of 500
    @PostMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberCompleteChatResponseDto completeChat(@RequestBody final ChamberCompleteChatRequestDto requestDto) {
        final var requestBo = ChamberCompleteChatRequestBo.builder() //
                .userId(requestDto.userId()) //
                .userMessage(requestDto.userMessage()) //
                .build();
        var responseMessage = "";
        try {
            responseMessage = chamberChatService.gpt35completeChat(requestBo);
        } catch (FeignException.BadRequest e) {
            final var errorLog = e.toString();
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete GPT3.5 chat due to bad request: {}", errorLog);
            chamberChatService.resetChatHistory(requestDto.userId());
            responseMessage = GPT_35_CHAT_BAD_REQUEST;
        } catch (RetryableException e) {
            final var errorLog = e.toString();
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to complete GPT3.5 chat due to feign retryable error: {}",
                    errorLog);
            responseMessage = GPT_35_CHAT_READ_TIME_OUT;
        }
        return ChamberCompleteChatResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    // TODO validation, return boolean and catch error
    @DeleteMapping(value = "/history/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberResetChatHistoryResponseDto deleteHistory(@PathVariable final long userId) {
        final var responseMessage = chamberChatService.resetChatHistory(userId);
        return ChamberResetChatHistoryResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    // TODO validation, return boolean and catch error
    @PutMapping(value = "/messages/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberUpdateSystemMessageResponseDto updateSystemMessage(@PathVariable final long userId,
                                                                     @RequestBody final ChamberUpdateSystemMessageRequestDto requestDto) {
        final var responseMessage = chamberChatService.updateSystemMessage(userId, requestDto.systemMessage());
        return ChamberUpdateSystemMessageResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

}