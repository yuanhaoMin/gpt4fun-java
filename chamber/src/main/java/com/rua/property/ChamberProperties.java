package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record ChamberProperties(int estimatedPromptLength, int maxPromptTokens,
                                int maxTotalTokens) {
}