package com.rua.service;

import com.rua.model.request.ChamberTranscriptionRequestDto;
import com.rua.model.request.OpenAITranscriptionRequestDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChamberAudioService {

    private final OpenAIClientService openAIClientService;

    public OpenAITranscriptionResponseDto transcription(final ChamberTranscriptionRequestDto request) {
        final var openAIRequest = OpenAITranscriptionRequestDto.builder() //
                .model(request.model()) //
                .file(request.file()) //
                .build();
        return openAIClientService.transcription(openAIRequest);
    }

}