package com.rua.model;

import com.plexpt.chatgpt.entity.chat.Message;
import lombok.Builder;

import java.util.List;

@Builder
public record GPTCompleteChatRequest(List<Message> messages, int maxCompletionTokens) {
}