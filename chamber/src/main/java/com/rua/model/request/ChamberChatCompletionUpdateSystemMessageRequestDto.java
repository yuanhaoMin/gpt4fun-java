package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChamberChatCompletionUpdateSystemMessageRequestDto(@JsonProperty("systemMessage") String systemMessage) {
}