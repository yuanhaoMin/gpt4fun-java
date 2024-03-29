package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record OpenAIProperties(int maxTokensCompletion, //
                               int connectTimeoutMillis, //
                               int responseTimeoutMillis, //
                               int writeTimeoutMillis, //
                               int readTimeoutMillis) {
}