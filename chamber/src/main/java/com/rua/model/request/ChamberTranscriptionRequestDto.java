package com.rua.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ChamberTranscriptionRequestDto(@JsonProperty("model") //
                                             String model, //
                                             @JsonProperty("file") //
                                             MultipartFile file) {
}