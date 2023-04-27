package com.rua.controller;

import com.rua.model.request.ChamberUpdateCompletionRequestBo;
import com.rua.model.request.ChamberUpdateCompletionRequestDto;
import com.rua.model.response.ChamberCompletionWithStreamResponseDto;
import com.rua.model.response.ChamberUpdateCompletionResponseDto;
import com.rua.service.ChamberCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static com.rua.constant.ChamberPathConstants.*;

@RequestMapping(value = CHAMBER_COMPLETION_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberCompletionController {

    private final ChamberCompletionService chamberCompletionService;

    @GetMapping(path = CHAMBER_COMPLETION_COMPLETION_WITH_STREAM_PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChamberCompletionWithStreamResponseDto> completionWithStream(
            @RequestParam(name = "username") final String username) {
        return chamberCompletionService.completionWithStream(username);
    }

    @PutMapping(value = CHAMBER_COMPLETION_UPDATE_COMPLETION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChamberUpdateCompletionResponseDto updateCompletion(final Authentication authentication,
                                                               @Valid @RequestBody final ChamberUpdateCompletionRequestDto requestDto) {
        final var requestBo = ChamberUpdateCompletionRequestBo.builder() //
                .model(requestDto.model()) //
                .message(requestDto.message()) //
                .temperature(requestDto.temperature()) //
                .username(authentication.getName()) //
                .build();
        final var responseMessage = chamberCompletionService.updateUserCompletion(requestBo);
        return ChamberUpdateCompletionResponseDto.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

}