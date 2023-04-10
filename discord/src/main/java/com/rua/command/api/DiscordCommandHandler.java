package com.rua.command.api;

import com.rua.service.DiscordChatService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public interface DiscordCommandHandler {

    String getCommandName();

    Mono<Void> handleCommand(DiscordChatService discordChatService, ChatInputInteractionEvent event, String guildId);

}