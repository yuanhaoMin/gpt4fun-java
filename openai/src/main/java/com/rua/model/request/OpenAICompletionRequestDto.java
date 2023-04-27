package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OpenAICompletionRequestDto(@JsonProperty("model") String model, //
                                         @JsonProperty("prompt") String prompt, //
                                         @JsonProperty("max_tokens") int maxTokens, //
                                         @JsonProperty("temperature") double temperature, //
                                         @JsonProperty("stream") boolean useStream) {
}