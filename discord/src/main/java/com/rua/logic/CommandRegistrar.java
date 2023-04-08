package com.rua.logic;

import com.rua.command.api.CommandRequestBuilder;
import com.rua.property.DiscordProperties;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandRegistrar {

    private final DiscordProperties discordProperties;

    private final List<CommandRequestBuilder> requestBuilders;

    public void register(final GatewayDiscordClient client) {
        final var applicationCommandRequests = requestBuilders.stream() //
                .map(CommandRequestBuilder::build) //
                .toList();
        client.getRestClient().getApplicationService().bulkOverwriteGlobalApplicationCommand(
                discordProperties.applicationId(), applicationCommandRequests).subscribe();
    }

}