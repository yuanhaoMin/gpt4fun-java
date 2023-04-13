package com.rua.service;

import com.rua.model.request.OpenAISpeechToTextRequest;
import com.rua.model.response.OpenAIWhisperTranscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChamberSpeechService {

    private final OpenAIClientService openAIClientService;

    public OpenAIWhisperTranscriptionResponse convertVoiceToText(final OpenAISpeechToTextRequest request) {
        return openAIClientService.createTranscription(request);
    }

}