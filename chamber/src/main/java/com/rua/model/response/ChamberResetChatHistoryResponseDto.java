package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberResetChatHistoryResponseDto(@JsonProperty("responseMessage") String responseMessage) {
}