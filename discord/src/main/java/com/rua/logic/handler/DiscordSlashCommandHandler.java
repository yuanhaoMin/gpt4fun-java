package com.rua.logic.handler;

import com.rua.logic.command.DiscordCommandHandler;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscordSlashCommandHandler implements DiscordEventHandler<ChatInputInteractionEvent> {

    private final List<DiscordCommandHandler> discordCommandHandlers;

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(final ChatInputInteractionEvent event) {
        final var guildId = event.getInteraction().getGuildId();
        return guildId.map(snowflake -> discordCommandHandlers.stream() //
                        .filter(discordCommandHandler -> discordCommandHandler.getCommandName()
                                .equals(event.getCommandName())) //
                        .findFirst() //
                        .map(discordCommandHandler -> discordCommandHandler.handleCommand(event, snowflake.asString()))
                        .orElse(Mono.empty())) //
                .orElseGet(Mono::empty);
    }

}