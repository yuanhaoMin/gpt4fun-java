package com.rua;

import com.rua.config.OpenAIClientConfig;
import com.rua.model.request.OpenAIGPT35ChatRequestDto;
import com.rua.model.response.OpenAIGPT35ChatWithoutStreamResponseDto;
import com.rua.model.response.OpenAIWhisperTranscriptionResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static com.rua.constant.OpenAIConstants.*;

@FeignClient(
        name = "openai-service",
        url = OPENAI_API_BASE_URL,
        configuration = OpenAIClientConfig.class
)
public interface OpenAIClient {

    @PostMapping(value = OPENAI_API_CHAT_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    String chatWithStream(
            @RequestBody OpenAIGPT35ChatRequestDto openAIGPT35ChatRequestDto);

    @PostMapping(value = OPENAI_API_CHAT_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    OpenAIGPT35ChatWithoutStreamResponseDto chatWithoutStream(
            @RequestBody OpenAIGPT35ChatRequestDto openAIGPT35ChatRequestDto);

    @PostMapping(value = OPENAI_API_CREATE_TRANSCRIPTION_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    OpenAIWhisperTranscriptionResponseDto createTranscription(
            // All parameters must be listed with @RequestPart, use one DTO will not work!
            @RequestPart("model") String model, @RequestPart("file") MultipartFile file);

}