package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rua.model.request.OpenAIChatCompletionMessage;

public record OpenAIChatCompletionWithoutStreamChoice(@JsonProperty("index") Integer index, //
                                                      @JsonProperty("message") OpenAIChatCompletionMessage message, //
                                                      @JsonProperty("finish_reason") String finishReason) {
}