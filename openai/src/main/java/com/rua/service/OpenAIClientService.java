package com.rua.service;

import com.rua.OpenAIFeignClient;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
import com.rua.model.request.OpenAICompletionRequestDto;
import com.rua.model.request.OpenAITranscriptionRequestDto;
import com.rua.model.response.OpenAIChatCompletionWithoutStreamResponseDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

import static com.rua.constant.OpenAIConstants.*;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {

    private final OpenAIFeignClient openAIFeignClient;

    // Do not create new webClient for each request
    private final WebClient webClient;

    public Flux<String> chatCompletionWithStream(final OpenAIChatCompletionRequestDto request) {
        if (!request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = true");
        }
        return webClient.post() //
                .uri(OPENAI_API_CHAT_COMPLETION_URL) //
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .body(BodyInserters.fromValue(request)) //
                .retrieve() //
                .bodyToFlux(String.class)
                // It will throw RetryExhaustedException if max attempts is reached
                .retryWhen(Retry.backoff(3, Duration.ofMillis(800)))
                // There is no point to throw RetryExhaustedException, so we map it to its cause
                .onErrorMap(Throwable::getCause);
    }

    public OpenAIChatCompletionWithoutStreamResponseDto chatCompletionWithoutStream(final OpenAIChatCompletionRequestDto request) {
        if (request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = false");
        }
        return openAIFeignClient.chatCompletionWithoutStream(request);
    }

    public Flux<String> completionWithStream(final OpenAICompletionRequestDto request) {
        if (!request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = true");
        }
        return webClient.post() //
                .uri(OPENAI_API_COMPLETION_URL) //
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .body(BodyInserters.fromValue(request)) //
                .retrieve() //
                .bodyToFlux(String.class)
                // It will throw RetryExhaustedException if max attempts is reached
                .retryWhen(Retry.backoff(3, Duration.ofMillis(800)))
                // There is no point to throw RetryExhaustedException, so we map it to its cause
                .onErrorMap(Throwable::getCause);
    }

    public OpenAITranscriptionResponseDto transcription(final OpenAITranscriptionRequestDto request) {
        final var model = request.model();
        final var audioFile = request.file();
        return openAIFeignClient.transcription(model, audioFile);
    }

}