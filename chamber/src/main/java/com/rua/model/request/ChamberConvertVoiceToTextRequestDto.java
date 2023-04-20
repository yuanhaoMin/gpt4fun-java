package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ChamberConvertVoiceToTextRequestDto(@JsonProperty("file") MultipartFile file) {
}