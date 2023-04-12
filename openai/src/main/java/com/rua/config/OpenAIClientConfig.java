package com.rua.config;

import com.rua.property.OpenAIProperties;
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
@EnableFeignClients
@PropertySource("classpath:openai.properties")
@RequiredArgsConstructor
public class OpenAIClientConfig {

    private final OpenAIProperties openAIProperties;

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return request -> request.header("Authorization", "Bearer " + openAIProperties.apiKey());
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(openAIProperties.connectTimeout(), TimeUnit.MILLISECONDS,
                openAIProperties.readTimeout(), TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

}