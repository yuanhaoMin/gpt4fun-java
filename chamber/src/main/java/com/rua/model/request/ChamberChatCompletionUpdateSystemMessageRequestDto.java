package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChamberChatCompletionUpdateSystemMessageRequestDto(@JsonProperty("systemMessage") //
                                                                 @NotBlank //
                                                                 String systemMessage) {
}