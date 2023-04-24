package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberChatCompletionResetChatHistoryResponseDto(
        @JsonProperty("responseMessage") String responseMessage) {
}