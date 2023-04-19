package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record OpenAIGPT35ChatWithoutStreamResponseDto(@JsonProperty("id") String id,
                                                      @JsonProperty("object") String object,
                                                      @JsonProperty("model") String model,
                                                      @JsonProperty("created") LocalDate created,
                                                      @JsonProperty("choices") List<OpenAIGPT35ChatWithoutStreamChoice> choices,
                                                      @JsonProperty("usage") OpenAIGPT35ChatUsage usage) {
}