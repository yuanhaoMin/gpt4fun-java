package com.rua.logic;

import com.rua.command.api.CommandHandler;
import com.rua.logic.api.EventHandler;
import com.rua.service.DiscordChatService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SlashCommandHandler implements EventHandler<ChatInputInteractionEvent> {

    private final List<CommandHandler> commandHandlers;

    private final DiscordChatService discordChatService;

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(final ChatInputInteractionEvent event) {
        final var guildId = event.getInteraction().getGuildId();
        return guildId.map(snowflake -> commandHandlers.stream() //
                        .filter(commandHandler -> commandHandler.getCommandName().equals(event.getCommandName())) //
                        .findFirst() //
                        .map(commandHandler -> commandHandler.handleCommand(discordChatService, event,
                                snowflake.asString())).orElse(Mono.empty())) //
                .orElseGet(Mono::empty);
    }

}