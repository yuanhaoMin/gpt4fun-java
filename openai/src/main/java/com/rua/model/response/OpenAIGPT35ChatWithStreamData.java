package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAIGPT35ChatWithStreamData(@JsonProperty("id") String id, @JsonProperty("object") String object,
                                            @JsonProperty("model") String model,
                                            @JsonProperty("created") String created,
                                            @JsonProperty("choices") List<OpenAIGPT35ChatWithStreamChoice> choices) {
}