package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChamberUserLoginRequestDto(
        @JsonProperty("username") //
        @Email(message = "Email must be valid, xxx@qiankuniot.com", regexp = "^[a-zA-Z0-9_-]+@qiankuniot\\.com$") //
        @Nonnull //
        String username, //
        @JsonProperty("password") //
        @NotBlank(message = "Password cannot be empty") //
        @Nonnull //
        String password) {
}