package com.rua.model.request;

import lombok.Builder;

@Builder
public record ChamberChatCompletionRequestBo(String model, String username, String userMessage, double temperature) {
}