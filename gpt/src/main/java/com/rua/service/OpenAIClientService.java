package com.rua.service;

import com.rua.config.OpenAIClient;
import com.rua.config.OpenAIClientConfig;
import com.rua.model.request.ChatGPTRequest;
import com.rua.model.request.Message;
import com.rua.model.request.WhisperTranscriptionRequest;
import com.rua.model.response.ChatGPTResponse;
import com.rua.model.response.WhisperTranscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIClientService {

    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    public ChatGPTResponse chat(List<Message> messages) {
        ChatGPTRequest chatGPTRequest = ChatGPTRequest.builder()
                .model(openAIClientConfig.getModel())
                .messages(messages)
                .build();
        return openAIClient.chat(chatGPTRequest);
    }

    public WhisperTranscriptionResponse createTranscription(MultipartFile file) {
        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model(openAIClientConfig.getAudioModel())
                .file(file)
                .build();
        return openAIClient.createTranscription(whisperTranscriptionRequest);
    }

}