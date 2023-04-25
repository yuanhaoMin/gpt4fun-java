package com.rua.config;

import com.rua.property.OpenAIProperties;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
@EnableFeignClients(basePackages = "com.rua")
@PropertySource("classpath:openai.properties")
@RequiredArgsConstructor
public class OpenAIFeignClientConfig {

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
                openAIProperties.responseTimeout(), TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default();
    }

}