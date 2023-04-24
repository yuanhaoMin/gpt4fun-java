package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAITranscriptionResponseDto(@JsonProperty("text") String text) {
}