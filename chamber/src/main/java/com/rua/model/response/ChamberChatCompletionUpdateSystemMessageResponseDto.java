package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberChatCompletionUpdateSystemMessageResponseDto(
        @JsonProperty("responseMessage") String responseMessage) {
}