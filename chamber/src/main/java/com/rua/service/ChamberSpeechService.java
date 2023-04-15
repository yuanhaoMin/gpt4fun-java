package com.rua.service;

import com.rua.model.request.OpenAISpeechToTextRequestDto;
import com.rua.model.response.OpenAIWhisperTranscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChamberSpeechService {

    private final OpenAIClientService openAIClientService;

    public OpenAIWhisperTranscriptionResponseDto convertVoiceToText(final OpenAISpeechToTextRequestDto request) {
        return openAIClientService.createTranscription(request);
    }

}