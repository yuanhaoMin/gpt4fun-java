package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rua.model.request.OpenAIChatCompletionMessage;

public record OpenAIChatCompletionWithStreamChoice(@JsonProperty("index") Integer index,
                                                   @JsonProperty("delta") OpenAIChatCompletionMessage message,
                                                   @JsonProperty("finish_reason") String finishReason) {
}