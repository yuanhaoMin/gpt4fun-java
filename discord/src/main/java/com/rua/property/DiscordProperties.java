package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record DiscordProperties(long applicationId, String botToken, int estimatedPromptLength, int maxPromptTokens,
                                int maxTotalTokens) {
}