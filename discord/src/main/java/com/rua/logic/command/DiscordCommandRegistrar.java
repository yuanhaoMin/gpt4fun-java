package com.rua.logic.command;

import com.rua.property.DiscordProperties;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscordCommandRegistrar {

    private final DiscordProperties discordProperties;

    private final List<DiscordCommandRequestBuilder> requestBuilders;

    public void register(final GatewayDiscordClient client) {
        final var applicationCommandRequests = requestBuilders.stream() //
                .map(DiscordCommandRequestBuilder::build) //
                .toList();
        client.getRestClient().getApplicationService().bulkOverwriteGlobalApplicationCommand(
                discordProperties.applicationId(), applicationCommandRequests).subscribe();
    }

}