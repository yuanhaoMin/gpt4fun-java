package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIGPT35ChatMessage(@JsonProperty("role") String role, @JsonProperty("content") String content) {
}