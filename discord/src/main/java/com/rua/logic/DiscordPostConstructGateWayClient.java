package com.rua.logic;

import com.rua.logic.command.DiscordCommandRegistrar;
import com.rua.logic.handler.DiscordEventHandler;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscordPostConstructGateWayClient<T extends Event> {

    private final DiscordCommandRegistrar discordCommandRegistrar;

    private final List<DiscordEventHandler<T>> discordEventHandlers;

    private final GatewayDiscordClient gatewayDiscordClient;

    @PostConstruct
    public void init() {
        // Collect and register all commands
        discordCommandRegistrar.register(gatewayDiscordClient);
        for (DiscordEventHandler<T> handler : discordEventHandlers) {
            gatewayDiscordClient.getEventDispatcher() //
                    .on(handler.getEventType()) //
                    // Error handling inside flatMap: https://discord4j.readthedocs.io/en/latest/Error-Handling/
                    .flatMap(event -> handler.execute(event).onErrorResume(handler::handleError)) //
                    .subscribe();
        }
    }

}