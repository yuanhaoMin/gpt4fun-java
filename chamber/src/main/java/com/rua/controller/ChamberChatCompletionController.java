package com.rua.controller;

import com.rua.constant.ChamberPathConstants;
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

import static com.rua.constant.ChamberPathConstants.*;
import static com.rua.constant.OpenAIConstants.OPENAI_MODEL_GPT_35_TURBO;

@RequestMapping(value = ChamberPathConstants.CHAMBER_CHAT_COMPLETION_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberChatCompletionController {

    private final ChamberChatCompletionService chamberChatCompletionService;

    @PostMapping(value = CHAMBER_CHAT_COMPLETION_WITHOUT_STREAM_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = CHAMBER_CHAT_COMPLETION_WITH_STREAM_PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCompletionWithStream(
            @RequestParam(name = "username") final String username) {
        final var requestBo = ChamberChatCompletionRequestBo.builder() //
                .model(OPENAI_MODEL_GPT_35_TURBO) //
                .temperature(0) //
                .username(username) //
                .userMessage("Hi") //
                .build();
        return chamberChatCompletionService.chatCompletionWithStream(requestBo);
    }

    @DeleteMapping(value = CHAMBER_CHAT_COMPLETION_RESET_CHAT_HISTORY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberChatCompletionResetChatHistoryResponseDto resetChatHistory(final Authentication authentication) {
        final var responseMessage = chamberChatCompletionService.resetChatHistory(authentication.getName());
        return ChamberChatCompletionResetChatHistoryResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @PutMapping(value = CHAMBER_CHAT_COMPLETION_UPDATE_SYSTEM_MESSAGE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberChatCompletionUpdateSystemMessageResponseDto updateSystemMessage(final Authentication authentication,
                                                                                   @RequestBody final ChamberChatCompletionUpdateSystemMessageRequestDto requestDto) {
        final var responseMessage = chamberChatCompletionService.updateSystemMessage(authentication.getName(),
                requestDto.systemMessage());
        return ChamberChatCompletionUpdateSystemMessageResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

}