package com.rua.service;

import com.rua.OpenAIFeignClient;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
import com.rua.model.request.OpenAITranscriptionRequestDto;
import com.rua.model.response.OpenAIChatCompletionWithoutStreamResponseDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
import com.rua.property.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import static com.rua.constant.OpenAIConstants.*;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {

    private final OpenAIFeignClient openAIFeignClient;

    private final OpenAIProperties openAIProperties;

    private final HttpClient webHttpClient;

    public Flux<String> chatCompletionWithStream(final OpenAIChatCompletionRequestDto request) {
        if (!request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = true");
        }
        final var webClient = WebClient.builder() //
                .baseUrl(OPENAI_API_BASE_URL + OPENAI_API_CHAT_COMPLETION_URL) //
                .clientConnector(new ReactorClientHttpConnector(webHttpClient)) //
                .build();
        return webClient.post() //
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .header("Authorization", "Bearer " + openAIProperties.apiKey()) //
                .body(BodyInserters.fromValue(request)) //
                .retrieve() //
                .bodyToFlux(String.class);
    }

    public OpenAIChatCompletionWithoutStreamResponseDto chatCompletionWithoutStream(
            final OpenAIChatCompletionRequestDto request) {
        if (request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = false");
        }
        return openAIFeignClient.chatCompletionWithoutStream(request);
    }

    public OpenAITranscriptionResponseDto transcription(final OpenAITranscriptionRequestDto request) {
        final var model = request.model();
        final var audioFile = request.file();
        return openAIFeignClient.transcription(model, audioFile);
    }

}