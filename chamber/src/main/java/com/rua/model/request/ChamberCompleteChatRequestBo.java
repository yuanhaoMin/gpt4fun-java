package com.rua.model.request;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChamberCompleteChatRequestBo(String username, String userMessage, @Nullable LocalDateTime lastChatTime,
                                           double temperature) {
}