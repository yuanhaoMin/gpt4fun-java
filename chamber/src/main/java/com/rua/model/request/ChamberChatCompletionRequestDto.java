package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChamberChatCompletionRequestDto(@JsonProperty("userMessage") //
                                              @NotBlank //
                                              String userMessage, //
                                              @JsonProperty("temperature") //
                                              @DecimalMin(value = "0", message = "Temperature must be at least 0") //
                                              @DecimalMax(value = "2", message = "Temperature must be at most 2") //
                                              double temperature) {
}