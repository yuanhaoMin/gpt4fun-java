package com.rua.model.request;

import lombok.Builder;

@Builder
public record ChamberCompleteChatRequestDto(Long userId, String systemMessage,
                                            String userMessage) {
}