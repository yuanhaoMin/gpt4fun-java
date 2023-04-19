package com.rua.model.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChamberCompleteChatRequestDto(@NotBlank String userMessage,
                                            @DecimalMin(value = "0", message = "Temperature must be at least 0") //
                                            @DecimalMax(value = "2", message = "Temperature must be at most 2") //
                                            double temperature) {
}