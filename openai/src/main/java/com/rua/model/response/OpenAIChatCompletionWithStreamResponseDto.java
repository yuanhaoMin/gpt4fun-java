package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAIChatCompletionWithStreamResponseDto(@JsonProperty("id") String id,
                                                        @JsonProperty("object") String object,
                                                        @JsonProperty("model") String model,
                                                        @JsonProperty("created") String created,
                                                        @JsonProperty("choices") List<OpenAIChatCompletionWithStreamChoice> choices) {
}