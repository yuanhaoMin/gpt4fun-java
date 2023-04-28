package com.rua.model.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DiscordChatCompletionRequestBo(String guildId, LocalDateTime lastChatTime, String username,
                                             String userMessage) {

}