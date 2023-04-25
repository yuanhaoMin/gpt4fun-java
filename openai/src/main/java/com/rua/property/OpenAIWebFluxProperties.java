package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record OpenAIWebFluxProperties(int connectTimeout, int writeTimeout, int readTimeout) {
}