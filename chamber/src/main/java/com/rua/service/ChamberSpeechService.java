package com.rua.service;

import com.rua.model.request.ChamberConvertVoiceToTextRequestDto;
import com.rua.model.request.OpenAISpeechToTextRequestDto;
import com.rua.model.response.OpenAIWhisperTranscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.rua.constant.OpenAIConstants.OPENAI_MODEL_WHISPER_1;

@RequiredArgsConstructor
@Service
public class ChamberSpeechService {

    private final OpenAIClientService openAIClientService;

    public OpenAIWhisperTranscriptionResponseDto convertVoiceToText(final ChamberConvertVoiceToTextRequestDto request) {
        final var openAIRequest = OpenAISpeechToTextRequestDto.builder() //
                .model(OPENAI_MODEL_WHISPER_1) //
                .file(request.file()) //
                .build();
        return openAIClientService.whisperCreateTranscription(openAIRequest);
    }

}