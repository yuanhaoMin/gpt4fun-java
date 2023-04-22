package com.rua.logic.handler;

import com.rua.property.DiscordProperties;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.rua.constant.DiscordConstants.LOG_PREFIX_DISCORD;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordDisconnectHandler implements DiscordEventHandler<DisconnectEvent> {

    private final ApplicationContext applicationContext;

    private final DiscordProperties discordProperties;

    @Override
    public Class<DisconnectEvent> getEventType() {
        return DisconnectEvent.class;
    }

    @Override
    public Mono<Void> execute(final DisconnectEvent event) {
        log.warn(LOG_PREFIX_DISCORD + "Disconnected from Discord, recreating GatewayDiscordClient...");
        final var newGatewayDiscordClient = DiscordClientBuilder.create(discordProperties.botToken()) //
                .build() //
                .login() //
                .block();
        if (newGatewayDiscordClient == null) {
            log.error(LOG_PREFIX_DISCORD + "Failed to recreate GatewayDiscordClient");
        } else {
            final DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
            registry.destroySingleton("GatewayDiscordClient");
            registry.registerSingleton("GatewayDiscordClient", newGatewayDiscordClient);
            log.info(LOG_PREFIX_DISCORD + "Successfully recreated GatewayDiscordClient");
        }
        return Mono.empty();
    }

}