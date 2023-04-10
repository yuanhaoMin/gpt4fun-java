package com.rua.service;

import com.plexpt.chatgpt.entity.chat.Message;
import com.rua.model.ChamberChatMessageRequest;
import com.rua.model.GPTCompleteChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChamberChatService {

    private final GPTChatService gptChatService;

    public String gpt35completeChat(final ChamberChatMessageRequest chamberChatMessageRequest) {
        final var messages = List.of(Message.ofSystem(chamberChatMessageRequest.systemMessage()),
                Message.of(chamberChatMessageRequest.userMessage()));
        final var request = GPTCompleteChatRequest.builder() //
                .messages(messages) //
                .maxCompletionTokens(1000) //
                .build();
        final var chatCompletionResponse = gptChatService.gpt35CompleteChat(request);
        return chatCompletionResponse.getChoices().get(0).getMessage().getContent();
    }

}