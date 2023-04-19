package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAIGPT35ChatWithStreamResponseDto(@JsonProperty("data") List<OpenAIGPT35ChatWithStreamChoice> data) {
}