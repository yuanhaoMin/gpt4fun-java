package com.rua.controller;

import com.rua.service.ChamberChatCompletionService;
import io.netty.handler.timeout.ReadTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rua.constant.ChamberConstants.ERROR_STREAM_READ_TIMEOUT;
import static com.rua.constant.ChamberPathConstants.CHAMBER_CHAT_COMPLETION_CHAT_COMPLETION_WITH_STREAM_PATH;
import static com.rua.constant.ChamberPathConstants.CHAMBER_CHAT_COMPLETION_CONTROLLER_PATH;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@WebFluxTest(excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class, //
        value = ChamberChatCompletionController.class) //
class ChamberChatCompletionControllerITest {

    private final WebTestClient webTestClient;

    @MockBean
    private ChamberChatCompletionService chamberChatCompletionService;

    @Autowired
    public ChamberChatCompletionControllerITest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void testChatCompletionWithStream_readTimeoutException() {
        // Given
        final var username = "username";
        final var path = CHAMBER_CHAT_COMPLETION_CONTROLLER_PATH + CHAMBER_CHAT_COMPLETION_CHAT_COMPLETION_WITH_STREAM_PATH;
        final var uri = UriComponentsBuilder.fromPath(path) //
                .queryParam("username", username) //
                .build() //
                .toUri();
        final var wrappedException = new WebClientRequestException(
                new ReadTimeoutException(), HttpMethod.GET, uri, HttpHeaders.EMPTY);
        when(chamberChatCompletionService.chatCompletionWithStream(username)).thenThrow(wrappedException);

        // When
        final var responseBody = webTestClient.get() //
                .uri(uri) //
                .exchange()
                // Then
                .expectStatus() //
                .isEqualTo(HttpStatus.GATEWAY_TIMEOUT) //
                .expectBody() //
                .returnResult();
        assertTrue(responseBody.toString().contains(ERROR_STREAM_READ_TIMEOUT));
    }

}