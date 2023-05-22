package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChamberUserLoginRequestDto(
        @JsonProperty("username") //
        @NotBlank(message = "Username cannot be empty") //
        String username, //
        @JsonProperty("password") //
        @NotBlank(message = "Password cannot be empty") //
        @Nonnull //
        String password) {
}