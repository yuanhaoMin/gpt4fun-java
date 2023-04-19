package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIGPT35ChatWithStreamChoice(@JsonProperty("index") Integer index,
                                              @JsonProperty("delta") OpenAIGPT35DeltaMessage delta,
                                              @JsonProperty("finish_reason") String finishReason) {
}