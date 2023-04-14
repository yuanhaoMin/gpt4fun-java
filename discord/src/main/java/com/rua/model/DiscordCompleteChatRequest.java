package com.rua.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DiscordCompleteChatRequest(String guildId, LocalDateTime lastChatTime, String userName,
                                         String userMessage) {

}