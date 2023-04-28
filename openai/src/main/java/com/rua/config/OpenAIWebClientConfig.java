package com.rua.config;

import com.rua.property.OpenAIProperties;
import com.rua.property.OpenAIWebClientProperties;
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
@EnableConfigurationProperties(OpenAIWebClientProperties.class)
@PropertySource("classpath:webclient.properties")
@RequiredArgsConstructor
public class OpenAIWebClientConfig {

    private final OpenAIProperties openAIProperties;

    private final OpenAIWebClientProperties openAIWebClientProperties;

    @Bean
    @DependsOn("webHttpClient")
    public WebClient webClient(final HttpClient httpClient) {
        httpClient.warmup().block();
        return WebClient.builder() //
                .baseUrl(OPENAI_API_BASE_URL) //
                .clientConnector(new ReactorClientHttpConnector(httpClient)) //
                .defaultHeader("Authorization", "Bearer " + openAIProperties.apiKey()) //
                .build();
    }

    @Bean(name = "webHttpClient")
    public HttpClient webHttpClient() {
        return HttpClient.create() //
                // Max time for a client to establish a connection with the server.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openAIWebClientProperties.connectTimeout()) //
                .doOnConnected(conn -> conn
                        // Max time for a client to send a request after establishing a connection.
                        .addHandlerLast(new WriteTimeoutHandler(openAIWebClientProperties.writeTimeout())) //
                        // Max time for a client to wait for a single response (In SSE) after sending a request.
                        .addHandlerLast(new ReadTimeoutHandler(openAIWebClientProperties.readTimeout())));
    }

}