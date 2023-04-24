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
import org.springframework.context.annotation.PropertySource;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
@EnableFeignClients(basePackages = "com.rua")
@PropertySource("classpath:openai.properties")
@RequiredArgsConstructor
public class OpenAIClientConfig {

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
        return new Request.Options(openAIProperties.connectTimeout(), TimeUnit.MILLISECONDS,
                openAIProperties.responseTimeoutWithoutStream(), TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default();
    }

    @Bean
    public HttpClient webFluxHttpClient() {
        return HttpClient.create() //
                // Max time for a client to establish a connection with the server.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openAIProperties.connectTimeout()) //
                .doOnConnected(conn -> conn
                        // Max time for a client to send a request after establishing a connection.
                        .addHandlerLast(new WriteTimeoutHandler(openAIProperties.writeTimeoutWithStream())) //
                        // Max time for a client to wait for a single response (In SSE) after sending a request.
                        .addHandlerLast(new ReadTimeoutHandler(openAIProperties.readTimeoutWithStream())));
    }

}