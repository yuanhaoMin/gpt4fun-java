package com.rua.model.request;

import lombok.Builder;

@Builder
public record ChamberCompleteChatRequestBo(String username, String userMessage, double temperature) {
}