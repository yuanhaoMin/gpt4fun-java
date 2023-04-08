package com.rua.config;

import com.plexpt.chatgpt.ChatGPT;
import com.rua.property.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
@PropertySource("classpath:openai.properties")
@RequiredArgsConstructor
public class GPTClientConfig {

    private final OpenAIProperties openAIProperties;

    @Bean
    public ChatGPT chatGPT() {
        return ChatGPT.builder() //
                .apiKeyList(openAIProperties.apiKeys()) //
                .timeout(openAIProperties.requestTimeOut()) //
                .apiHost(openAIProperties.apiHost()) //
                .build() //
                .init();
    }

}