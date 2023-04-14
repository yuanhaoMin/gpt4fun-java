package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rua.model.request.OpenAIGPT35ChatMessage;

public record OpenAIGPT35ChatChoice(Integer index,
                                    @JsonProperty("message") OpenAIGPT35ChatMessage message,
                                    String finishReason) {
}