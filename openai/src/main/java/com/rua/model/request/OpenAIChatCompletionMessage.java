package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIChatCompletionMessage(@JsonProperty("role") String role, //
                                          @JsonProperty("content") String content) {
}