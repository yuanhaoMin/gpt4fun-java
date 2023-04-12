package com.rua.config;

import com.rua.model.request.OpenAIGPT35ChatRequest;
import com.rua.model.request.OpenAIWhisperTranscriptionRequest;
import com.rua.model.response.OpenAIGPT35ChatResponse;
import com.rua.model.response.OpenAIWhisperTranscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.rua.constant.OpenAIConstants.*;

@FeignClient(
        name = "openai-service",
        url = OPENAI_API_BASE_URL,
        configuration = OpenAIClientConfig.class
)
public interface OpenAIClient {

    @PostMapping(value = OPENAI_API_CHAT_URL, headers = {"Content-Type=application/json"})
    OpenAIGPT35ChatResponse chat(@RequestBody OpenAIGPT35ChatRequest openAIGPT35ChatRequest);

    @PostMapping(value = OPENAI_API_CREATE_TRANSCRIPTION_URL, headers = {"Content-Type=multipart/form-data"})
    OpenAIWhisperTranscriptionResponse createTranscription(
            @ModelAttribute OpenAIWhisperTranscriptionRequest openAIWhisperTranscriptionRequest);

}