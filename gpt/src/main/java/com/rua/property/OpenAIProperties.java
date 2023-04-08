package com.rua.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties
public record OpenAIProperties(String apiHost, List<String> apiKeys, Integer requestTimeOut) {
}