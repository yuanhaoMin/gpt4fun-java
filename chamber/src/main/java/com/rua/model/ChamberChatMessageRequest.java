package com.rua.model;

import lombok.Builder;

@Builder
public record ChamberChatMessageRequest(Long userId, String systemMessage, String userMessage) {
}