package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record OpenAIGPT35ChatRequest(String model,
                                     @JsonProperty("messages") List<OpenAIGPT35ChatMessage> messages,
                                     @JsonProperty("temperature") double temperature) {
}