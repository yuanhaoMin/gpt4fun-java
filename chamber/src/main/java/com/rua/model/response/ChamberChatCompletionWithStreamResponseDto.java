package com.rua.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberChatCompletionWithStreamResponseDto(@JsonProperty("content") String content,
                                                         @JsonProperty("hasEnd") boolean hasEnd) {
}