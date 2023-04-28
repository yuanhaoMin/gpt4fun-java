package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberCompletionWithStreamResponseDto(@JsonProperty("content") String content,
                                                     @JsonProperty("hasEnd") boolean hasEnd) {
}