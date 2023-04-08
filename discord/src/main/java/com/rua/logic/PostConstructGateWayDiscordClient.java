package com.rua.logic;

import com.rua.logic.api.EventHandler;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostConstructGateWayDiscordClient<T extends Event> {

    private final CommandRegistrar commandRegistrar;

    private final List<EventHandler<T>> eventHandlers;

    private final GatewayDiscordClient gatewayDiscordClient;

    @PostConstruct
    public void init() {
        // Collect and register all commands
        commandRegistrar.register(gatewayDiscordClient);
        for (EventHandler<T> handler : eventHandlers) {
            gatewayDiscordClient.getEventDispatcher() //
                    .on(handler.getEventType()) //
                    .flatMap(handler::execute) //
                    .onErrorResume(handler::handleError) //
                    .subscribe();
        }
    }

}