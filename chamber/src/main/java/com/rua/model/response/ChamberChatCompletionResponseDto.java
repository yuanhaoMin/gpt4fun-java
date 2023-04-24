package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberChatCompletionResponseDto(@JsonProperty("responseMessage") String responseMessage) {
}