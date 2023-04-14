package com.rua.service;

import com.rua.model.ChamberChatMessageRequest;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.response.OpenAIGPT35ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rua.constant.OpenAIConstants.GPT35TURBO_SYSTEM;
import static com.rua.constant.OpenAIConstants.GPT35TURBO_USER;

@RequiredArgsConstructor
@Service
public class ChamberChatService {

    private final OpenAIClientService openAIClientService;

    public OpenAIGPT35ChatResponse gpt35completeChat(final ChamberChatMessageRequest chamberChatMessageRequest) {
        final var messages = List.of(
                new OpenAIGPT35ChatMessage(GPT35TURBO_SYSTEM, chamberChatMessageRequest.systemMessage()),
                new OpenAIGPT35ChatMessage(GPT35TURBO_USER, chamberChatMessageRequest.userMessage()));
        return openAIClientService.chat(messages);
    }

}