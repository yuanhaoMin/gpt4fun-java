package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberUpdateCompletionResponseDto(@JsonProperty("responseMessage") String responseMessage) {
}