package com.rua.model.request;

import lombok.Builder;

@Builder
public record ChamberCompletionRequestBo(String model, String username, String message, double temperature) {
}