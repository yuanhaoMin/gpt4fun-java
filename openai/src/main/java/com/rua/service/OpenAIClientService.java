package com.rua.service;

import com.rua.OpenAIClient;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.request.OpenAIGPT35ChatRequest;
import com.rua.model.request.OpenAISpeechToTextRequest;
import com.rua.model.response.OpenAIGPT35ChatResponse;
import com.rua.model.response.OpenAIWhisperTranscriptionResponse;
import com.rua.property.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {

    private final OpenAIClient openAIClient;
    private final OpenAIProperties openAIProperties;

    public OpenAIGPT35ChatResponse chat(final List<OpenAIGPT35ChatMessage> openAIGPT35ChatMessages) {
        final var openAIGPT35ChatRequest = OpenAIGPT35ChatRequest.builder().model(openAIProperties.gptModel())
                .messages(openAIGPT35ChatMessages).temperature(0.9).build();
        return openAIClient.chat(openAIGPT35ChatRequest);
    }

    public OpenAIWhisperTranscriptionResponse createTranscription(final OpenAISpeechToTextRequest request) {
        final var model = request.model();
        final var audioFile = request.file();
        return openAIClient.createTranscription(model, audioFile);
    }

}