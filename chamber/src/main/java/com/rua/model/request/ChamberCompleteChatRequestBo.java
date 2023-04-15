package com.rua.model.request;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChamberCompleteChatRequestBo(Long userId, @Nullable LocalDateTime lastChatTime, String userMessage) {
}