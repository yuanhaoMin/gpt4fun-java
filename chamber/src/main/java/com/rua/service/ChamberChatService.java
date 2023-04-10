package com.rua.service;

import com.rua.model.ChamberChatMessageRequest;
import com.rua.model.request.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChamberChatService {

    private final OpenAIClientService openAIClientService;

    public String gpt35completeChat(final ChamberChatMessageRequest chamberChatMessageRequest) {
        final var messages = List.of(new Message("system", chamberChatMessageRequest.systemMessage()),
                new Message("user", chamberChatMessageRequest.userMessage()));
        final var chatCompletionResponse = openAIClientService.chat(messages);
        return chatCompletionResponse.choices().get(0).message().content();
    }

}