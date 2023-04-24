package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record OpenAIChatCompletionResponseDto(@JsonProperty("id") String id,
                                              @JsonProperty("object") String object,
                                              @JsonProperty("model") String model,
                                              @JsonProperty("created") LocalDate created,
                                              @JsonProperty("choices") List<OpenAIChatCompletionChoice> choices,
                                              @JsonProperty("usage") OpenAIChatCompletionUsage usage) {
}