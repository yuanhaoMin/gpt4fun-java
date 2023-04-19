package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIWhisperTranscriptionResponseDto(@JsonProperty("text") String text) {
}