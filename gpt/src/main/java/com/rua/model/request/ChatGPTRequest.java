package com.rua.model.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatGPTRequest(String model, List<Message> messages) {
}