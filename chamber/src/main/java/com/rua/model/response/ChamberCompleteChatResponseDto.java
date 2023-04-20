package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberCompleteChatResponseDto(@JsonProperty("responseMessage") String responseMessage) {
}