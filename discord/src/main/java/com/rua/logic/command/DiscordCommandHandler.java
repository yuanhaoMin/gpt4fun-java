package com.rua.logic.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public interface DiscordCommandHandler {

    String getCommandName();

    Mono<Void> handleCommand(ChatInputInteractionEvent event,
                             String guildId);

}