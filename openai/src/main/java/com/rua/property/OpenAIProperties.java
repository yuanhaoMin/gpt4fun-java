package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record OpenAIProperties(String apiKey, int connectTimeout, int responseTimeoutWithoutStream,
                               int writeTimeoutWithStream, int readTimeoutWithStream) {
}