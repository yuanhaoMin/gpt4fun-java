package com.rua.model.request;

import lombok.Builder;

@Builder
public record ChamberUpdateSystemMessageRequestDto(String systemMessage) {
}