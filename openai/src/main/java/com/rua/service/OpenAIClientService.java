package com.rua.service;

import com.rua.config.OpenAIClient;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.request.OpenAIGPT35ChatRequest;
import com.rua.model.request.OpenAIWhisperTranscriptionRequest;
import com.rua.model.response.OpenAIGPT35ChatResponse;
import com.rua.model.response.OpenAIWhisperTranscriptionResponse;
import com.rua.property.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {

    private final OpenAIClient openAIClient;
    private final OpenAIProperties openAIProperties;

    public OpenAIGPT35ChatResponse chat(List<OpenAIGPT35ChatMessage> openAIGPT35ChatMessages) {
        OpenAIGPT35ChatRequest openAIGPT35ChatRequest = OpenAIGPT35ChatRequest.builder()
                .model(openAIProperties.gptModel())
                .messages(openAIGPT35ChatMessages)
                .build();
        return openAIClient.chat(openAIGPT35ChatRequest);
    }

    public OpenAIWhisperTranscriptionResponse createTranscription(MultipartFile multipartFile) {
        OpenAIWhisperTranscriptionRequest openAIWhisperTranscriptionRequest = OpenAIWhisperTranscriptionRequest.builder()
                .model(openAIProperties.audioModel())
                .file(multipartFile)
                .build();
        return openAIClient.createTranscription(openAIWhisperTranscriptionRequest);
    }

}