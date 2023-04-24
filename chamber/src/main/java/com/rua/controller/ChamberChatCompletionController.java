package com.rua.controller;

import com.rua.constant.ChamberControllerConstants;
import com.rua.model.request.ChamberChatCompletionRequestBo;
import com.rua.model.request.ChamberChatCompletionRequestDto;
import com.rua.model.request.ChamberChatCompletionUpdateSystemMessageRequestDto;
import com.rua.model.response.ChamberChatCompletionResetChatHistoryResponseDto;
import com.rua.model.response.ChamberChatCompletionResponseDto;
import com.rua.model.response.ChamberChatCompletionUpdateSystemMessageResponseDto;
import com.rua.service.ChamberChatCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static com.rua.constant.OpenAIConstants.OPENAI_MODEL_GPT_35_TURBO;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_CHAT_COMPLETION_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberChatCompletionController {

    private final ChamberChatCompletionService chamberChatCompletionService;

    @PostMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberChatCompletionResponseDto chatCompletionWithoutStream(final Authentication authentication,
                                                                        @Valid @RequestBody final ChamberChatCompletionRequestDto requestDto) {
        final var requestBo = ChamberChatCompletionRequestBo.builder() //
                .model(OPENAI_MODEL_GPT_35_TURBO) //
                .temperature(requestDto.temperature()) //
                .username(authentication.getName()) //
                .userMessage(requestDto.userMessage()) //
                .build();
        final var responseMessage = chamberChatCompletionService.chatCompletionWithoutStream(requestBo);
        return ChamberChatCompletionResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @PostMapping(value = "/stream-messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCompletionWithStream(final Authentication authentication,
                                                 @Valid @RequestBody final ChamberChatCompletionRequestDto requestDto) {
        final var requestBo = ChamberChatCompletionRequestBo.builder() //
                .model(OPENAI_MODEL_GPT_35_TURBO) //
                .temperature(requestDto.temperature()) //
                .username(authentication.getName()) //
                .userMessage(requestDto.userMessage()) //
                .build();
        return chamberChatCompletionService.chatCompletionWithStream(requestBo);
    }

    @DeleteMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberChatCompletionResetChatHistoryResponseDto resetChatHistory(final Authentication authentication) {
        final var responseMessage = chamberChatCompletionService.resetChatHistory(authentication.getName());
        return ChamberChatCompletionResetChatHistoryResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @PutMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberChatCompletionUpdateSystemMessageResponseDto updateSystemMessage(final Authentication authentication,
                                                                                   @RequestBody final ChamberChatCompletionUpdateSystemMessageRequestDto requestDto) {
        final var responseMessage = chamberChatCompletionService.updateSystemMessage(authentication.getName(),
                requestDto.systemMessage());
        return ChamberChatCompletionUpdateSystemMessageResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

}