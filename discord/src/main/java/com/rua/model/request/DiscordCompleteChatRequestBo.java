package com.rua.model.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DiscordCompleteChatRequestBo(String guildId, LocalDateTime lastChatTime, String username,
                                           String userMessage) {

}