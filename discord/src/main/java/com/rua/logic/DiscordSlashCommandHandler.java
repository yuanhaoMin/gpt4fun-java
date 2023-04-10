package com.rua.logic;

import com.rua.command.api.DiscordCommandHandler;
import com.rua.logic.api.DiscordEventHandler;
import com.rua.service.DiscordChatService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscordSlashCommandHandler implements DiscordEventHandler<ChatInputInteractionEvent> {

    private final List<DiscordCommandHandler> discordCommandHandlers;

    private final DiscordChatService discordChatService;

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(final ChatInputInteractionEvent event) {
        final var guildId = event.getInteraction().getGuildId();
        return guildId.map(snowflake -> discordCommandHandlers.stream() //
                        .filter(discordCommandHandler -> discordCommandHandler.getCommandName().equals(event.getCommandName())) //
                        .findFirst() //
                        .map(discordCommandHandler -> discordCommandHandler.handleCommand(discordChatService, event,
                                snowflake.asString())).orElse(Mono.empty())) //
                .orElseGet(Mono::empty);
    }

}