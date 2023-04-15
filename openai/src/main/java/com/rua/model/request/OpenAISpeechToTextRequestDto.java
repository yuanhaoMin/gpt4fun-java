package com.rua.model.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record OpenAISpeechToTextRequestDto(String model, MultipartFile file) {
}