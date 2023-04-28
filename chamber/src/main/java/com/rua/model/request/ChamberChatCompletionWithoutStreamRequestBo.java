package com.rua.model.request;

import lombok.Builder;

@Builder
public record ChamberChatCompletionWithoutStreamRequestBo(String model, String userMessage,
                                                          double temperature, String username) {
}