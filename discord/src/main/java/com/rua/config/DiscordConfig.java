package com.rua.config;

import com.rua.property.DiscordProperties;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(DiscordProperties.class)
@PropertySource("classpath:discord.properties")
@RequiredArgsConstructor
public class DiscordConfig {

    private final DiscordProperties discordProperties;

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        return DiscordClientBuilder.create(discordProperties.botToken()) //
                .build() //
                .login() //
                .block();
    }

}