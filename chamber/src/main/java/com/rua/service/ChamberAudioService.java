package com.rua.service;

import com.rua.constant.OpenAIModelInfo;
import com.rua.constant.OpenAITranscriptionModelEnum;
import com.rua.model.request.ChamberTranscriptionRequestDto;
import com.rua.model.request.OpenAITranscriptionRequestDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.rua.constant.ChamberConstants.ERROR_CAUSE_INVALID_MODEL;

@RequiredArgsConstructor
@Service
public class ChamberAudioService {

    private final OpenAIClientService openAIClientService;

    public OpenAITranscriptionResponseDto transcription(final ChamberTranscriptionRequestDto request, final String username) {
        try {
            OpenAIModelInfo.get(request.model(), OpenAITranscriptionModelEnum.class);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(ERROR_CAUSE_INVALID_MODEL, request.model(), username));
        }
        final var openAIRequest = OpenAITranscriptionRequestDto.builder() //
                .model(request.model()) //
                .file(request.file()) //
                .build();
        return openAIClientService.transcription(openAIRequest);
    }

}