package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIGPT35DeltaMessage(@JsonProperty("content") String content) {
}