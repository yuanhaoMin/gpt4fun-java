package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record OpenAIProperties(String apiKey, //
                               int maxTokensCompletion, //
                               int connectTimeoutMillis, //
                               int responseTimeoutMillis, //
                               int writeTimeout, //
                               int readTimeout) {
}