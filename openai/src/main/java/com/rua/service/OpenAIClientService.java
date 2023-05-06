package com.rua.service;

import com.rua.OpenAIFeignClient;
import com.rua.constant.OpenAIChatCompletionModelEnum;
import com.rua.model.request.OpenAIChatCompletionRequestDto;
import com.rua.model.request.OpenAICompletionRequestDto;
import com.rua.model.request.OpenAITranscriptionRequestDto;
import com.rua.model.response.OpenAIChatCompletionWithoutStreamResponseDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
import com.rua.property.OpenAIProperties;
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

    private final OpenAIProperties openAIProperties;

    private final OpenAIFeignClient openAIFeignClient;

    private final WebClient webClient;

    public Flux<String> chatCompletionWithStream(final String apiKey, final OpenAIChatCompletionRequestDto request) {
        if (!request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = true");
        }
        var timeoutMillis = Duration.ofMillis(openAIProperties.readTimeoutMillis());
        if (request.model().equals(OpenAIChatCompletionModelEnum.GPT4.getModelName())) {
            timeoutMillis = timeoutMillis.plusMillis(2000);
        }
        return webClient.post() //
                .uri(OPENAI_API_CHAT_COMPLETION_URL) //
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + apiKey) //
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .body(BodyInserters.fromValue(request)) //
                .retrieve() //
                .bodyToFlux(String.class)
                .timeout(timeoutMillis)
                // It will throw RetryExhaustedException if max attempts is reached
                .retryWhen(Retry.max(1))
                // There is no point to throw RetryExhaustedException, so we map it to its cause
                .onErrorMap(Throwable::getCause);
    }

    public OpenAIChatCompletionWithoutStreamResponseDto chatCompletionWithoutStream(final String apiKey,
                                                                                    final OpenAIChatCompletionRequestDto request) {
        if (request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = false");
        }
        final var authorization = BEARER_TOKEN_PREFIX + apiKey;
        return openAIFeignClient.chatCompletionWithoutStream(authorization, request);
    }

    public Flux<String> completionWithStream(final String apiKey, final OpenAICompletionRequestDto request) {
        if (!request.useStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = true");
        }
        return webClient.post() //
                .uri(OPENAI_API_COMPLETION_URL) //
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + apiKey) //
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .body(BodyInserters.fromValue(request)) //
                .retrieve() //
                .bodyToFlux(String.class)
                // It will throw RetryExhaustedException if max attempts is reached
                .retryWhen(Retry.backoff(3, Duration.ofMillis(800)))
                // There is no point to throw RetryExhaustedException, so we map it to its cause
                .onErrorMap(Throwable::getCause);
    }

    public OpenAITranscriptionResponseDto transcription(final String apiKey, final OpenAITranscriptionRequestDto request) {
        final var modelName = request.model();
        final var audioFile = request.file();
        final var authorization = BEARER_TOKEN_PREFIX + apiKey;
        return openAIFeignClient.transcription(authorization, modelName, audioFile);
    }

}