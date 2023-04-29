package com.rua;

import com.rua.config.OpenAIConfig;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
import com.rua.model.response.OpenAIChatCompletionWithoutStreamResponseDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
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
        configuration = OpenAIConfig.class
)
public interface OpenAIFeignClient {

    @PostMapping(value = OPENAI_API_CHAT_COMPLETION_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    OpenAIChatCompletionWithoutStreamResponseDto chatCompletionWithoutStream(
            @RequestBody OpenAIChatCompletionRequestDto openAIChatCompletionRequestDto);

    @PostMapping(value = OPENAI_API_TRANSCRIPTION_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    OpenAITranscriptionResponseDto transcription(
            // All parameters must be listed with @RequestPart, use one DTO will not work!
            @RequestPart("model") String model, @RequestPart("file") MultipartFile file);

}