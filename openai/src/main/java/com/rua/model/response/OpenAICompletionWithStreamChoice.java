package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAICompletionWithStreamChoice(@JsonProperty("text") String text, //
                                               @JsonProperty("index") Integer index, //
                                               @JsonProperty("logprobs") Integer logprobs, //
                                               @JsonProperty("finish_reason") String finishReason) {
}