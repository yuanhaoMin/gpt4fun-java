package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record OpenAIProperties(String apiKey, String audioModel, String gptModel, int connectTimeout, int readTimeout) {
}