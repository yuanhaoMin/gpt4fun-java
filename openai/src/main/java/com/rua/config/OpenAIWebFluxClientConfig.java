package com.rua.config;

import com.rua.property.OpenAIWebFluxProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static com.rua.constant.OpenAIConstants.OPENAI_API_BASE_URL;

@Configuration
@EnableConfigurationProperties(OpenAIWebFluxProperties.class)
@PropertySource("classpath:webflux.properties")
@RequiredArgsConstructor
public class OpenAIWebFluxClientConfig {

    private final OpenAIWebFluxProperties openAIWebFluxProperties;

    @Bean
    @DependsOn("webHttpClient")
    public WebClient webClient(final HttpClient httpClient) {
        return WebClient.builder() //
                .baseUrl(OPENAI_API_BASE_URL) //
                .clientConnector(new ReactorClientHttpConnector(httpClient)) //
                .build();
    }

    @Bean(name = "webHttpClient")
    public HttpClient webHttpClient() {
        return HttpClient.create() //
                // Max time for a client to establish a connection with the server.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openAIWebFluxProperties.connectTimeout()) //
                .doOnConnected(conn -> conn
                        // Max time for a client to send a request after establishing a connection.
                        .addHandlerLast(new WriteTimeoutHandler(openAIWebFluxProperties.writeTimeout())) //
                        // Max time for a client to wait for a single response (In SSE) after sending a request.
                        .addHandlerLast(new ReadTimeoutHandler(openAIWebFluxProperties.readTimeout())));
    }

}