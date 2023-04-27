package com.rua.model.request;

import lombok.Builder;

@Builder
public record ChamberUpdateCompletionRequestBo(String model, String message, double temperature, String username) {
}