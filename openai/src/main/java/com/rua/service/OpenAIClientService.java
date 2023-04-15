package com.rua.service;

import com.rua.OpenAIClient;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.request.OpenAIGPT35ChatRequestDto;
import com.rua.model.request.OpenAISpeechToTextRequestDto;
import com.rua.model.response.OpenAIGPT35ChatResponseDto;
import com.rua.model.response.OpenAIWhisperTranscriptionResponseDto;
import com.rua.property.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {

    private final OpenAIClient openAIClient;
    private final OpenAIProperties openAIProperties;

    public OpenAIGPT35ChatResponseDto chat(final List<OpenAIGPT35ChatMessage> openAIGPT35ChatMessages) {
        final var openAIGPT35ChatRequest = OpenAIGPT35ChatRequestDto.builder().model(openAIProperties.gptModel())
                .messages(openAIGPT35ChatMessages).temperature(0.9).build();
        return openAIClient.chat(openAIGPT35ChatRequest);
    }

    public OpenAIWhisperTranscriptionResponseDto createTranscription(final OpenAISpeechToTextRequestDto request) {
        final var model = request.model();
        final var audioFile = request.file();
        return openAIClient.createTranscription(model, audioFile);
    }

}