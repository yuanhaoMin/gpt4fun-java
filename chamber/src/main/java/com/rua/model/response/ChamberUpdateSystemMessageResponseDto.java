package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberUpdateSystemMessageResponseDto(@JsonProperty("responseMessage") String responseMessage) {
}