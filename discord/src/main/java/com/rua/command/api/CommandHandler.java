package com.rua.command.api;

import com.rua.service.DiscordChatService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class CommandHandler {

    public abstract String getCommandName();

    public abstract Mono<Void> handleCommand(DiscordChatService discordChatService, ChatInputInteractionEvent event,
                                             String guildId);

}