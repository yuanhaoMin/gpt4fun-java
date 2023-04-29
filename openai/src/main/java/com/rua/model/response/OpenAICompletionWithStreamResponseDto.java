package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAICompletionWithStreamResponseDto(@JsonProperty("id") String id, //
                                                    @JsonProperty("object") String object, //
                                                    @JsonProperty("created") String created, //
                                                    @JsonProperty("choices") List<OpenAICompletionWithStreamChoice> choices,
                                                    @JsonProperty("model") String model) {
}