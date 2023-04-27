package com.rua.controller;

import com.rua.model.request.ChamberChatCompletionWithoutStreamRequestBo;
import com.rua.model.request.ChamberChatCompletionWithoutStreamRequestDto;
import com.rua.model.request.ChamberUpdateSystemMessageRequestDto;
import com.rua.model.response.ChamberChatCompletionWithStreamResponseDto;
import com.rua.model.response.ChamberChatCompletionWithoutStreamResponseDto;
import com.rua.model.response.ChamberResetChatHistoryResponseDto;
import com.rua.model.response.ChamberUpdateSystemMessageResponseDto;
import com.rua.service.ChamberChatCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static com.rua.constant.ChamberPathConstants.*;
import static com.rua.constant.OpenAIConstants.OPENAI_MODEL_GPT_35_TURBO;

@RequestMapping(value = CHAMBER_CHAT_COMPLETION_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberChatCompletionController {

    private final ChamberChatCompletionService chamberChatCompletionService;

    @PostMapping(value = CHAMBER_CHAT_COMPLETION_WITHOUT_STREAM_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberChatCompletionWithoutStreamResponseDto chatCompletionWithoutStream(
            final Authentication authentication,
            @Valid @RequestBody final ChamberChatCompletionWithoutStreamRequestDto requestDto) {
        final var requestBo = ChamberChatCompletionWithoutStreamRequestBo.builder() //
                .model(OPENAI_MODEL_GPT_35_TURBO) //
                .temperature(requestDto.temperature()) //
                .userMessage(requestDto.userMessage()) //
                .username(authentication.getName()) //
                .build();
        final var responseMessage = chamberChatCompletionService.chatCompletionWithoutStream(requestBo);
        return ChamberChatCompletionWithoutStreamResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @GetMapping(path = CHAMBER_CHAT_COMPLETION_WITH_STREAM_PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChamberChatCompletionWithStreamResponseDto> chatCompletionWithStream(
            @RequestParam(name = "username") final String username) {
        return chamberChatCompletionService.chatCompletionWithStream(username);
    }

    @DeleteMapping(value = CHAMBER_CHAT_COMPLETION_RESET_CHAT_HISTORY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberResetChatHistoryResponseDto resetChatHistory(final Authentication authentication) {
        final var responseMessage = chamberChatCompletionService.resetChatHistory(authentication.getName());
        return ChamberResetChatHistoryResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @PutMapping(value = CHAMBER_CHAT_COMPLETION_UPDATE_SYSTEM_MESSAGE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberUpdateSystemMessageResponseDto updateSystemMessage(final Authentication authentication,
                                                                     @Valid @RequestBody final ChamberUpdateSystemMessageRequestDto requestDto) {
        final var responseMessage = chamberChatCompletionService.updateSystemMessage(authentication.getName(),
                requestDto.systemMessage());
        return ChamberUpdateSystemMessageResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

}