package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIChatCompletionWithoutStreamUsage(@JsonProperty("prompt_tokens") int promptTokens, //
                                                     @JsonProperty("completion_tokens") int completionTokens, //
                                                     @JsonProperty("total_tokens") int totalTokens) {

}