package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record OpenAIGPT35ChatRequestDto(String model, @JsonProperty("messages") List<OpenAIGPT35ChatMessage> messages,
                                        @JsonProperty("stream") boolean stream,
                                        @JsonProperty("temperature") double temperature) {
}