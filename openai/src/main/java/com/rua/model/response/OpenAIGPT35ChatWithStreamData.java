package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAIGPT35ChatWithStreamData(String id, String object, String model, String created,
                                            @JsonProperty("choices") List<OpenAIGPT35ChatWithStreamChoice> choices) {
}