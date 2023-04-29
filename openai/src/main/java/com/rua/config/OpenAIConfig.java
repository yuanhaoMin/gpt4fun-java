package com.rua.config;

import com.rua.property.OpenAIProperties;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

import static com.rua.constant.OpenAIConstants.OPENAI_API_BASE_URL;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
@EnableFeignClients(basePackages = "com.rua")
@PropertySource("classpath:openai.properties")
@RequiredArgsConstructor
public class OpenAIConfig {

    private final OpenAIProperties openAIProperties;

    // Will be inserted in the header of each request
    @Bean
    public RequestInterceptor feignAPIKeyInterceptor() {
        return request -> request.header("Authorization", "Bearer " + openAIProperties.apiKey());
    }

    // Needed to log the request and response
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(openAIProperties.connectTimeoutMillis(), TimeUnit.MILLISECONDS,
                openAIProperties.responseTimeoutMillis(), TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default();
    }

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
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openAIProperties.connectTimeoutMillis()) //
                .doOnConnected(conn -> conn
                        // Max time for a client to send a request after establishing a connection.
                        .addHandlerLast(new WriteTimeoutHandler(openAIProperties.writeTimeout())) //
                        // Max time for a client to wait for a single response (In SSE) after sending a request.
                        .addHandlerLast(new ReadTimeoutHandler(openAIProperties.readTimeout())));
    }

}