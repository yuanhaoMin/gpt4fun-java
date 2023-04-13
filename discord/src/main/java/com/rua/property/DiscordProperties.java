package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record DiscordProperties(long applicationId, int maxPromptTokens, String token) {
}