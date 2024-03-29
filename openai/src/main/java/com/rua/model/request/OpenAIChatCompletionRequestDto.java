package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record OpenAIChatCompletionRequestDto(@JsonProperty("model") String model, //
                                             @JsonProperty("messages") List<OpenAIChatCompletionMessage> messages, //
                                             @JsonProperty("temperature") double temperature, //
                                             @JsonProperty("stream") boolean useStream) {
}