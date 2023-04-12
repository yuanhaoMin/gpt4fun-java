package com.rua.command;

import com.rua.command.api.DiscordCommandHandler;
import com.rua.command.api.DiscordCommandRequestBuilder;
import com.rua.service.DiscordChatService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.rua.constant.DiscordConstants.*;


@Component
@Slf4j
public class DiscordCommandClearChatHistory implements DiscordCommandHandler, DiscordCommandRequestBuilder {

    @Override
    public ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder() //
                .name(COMMAND_CLEAR_CHAT_HISTORY_NAME) //
                .description(COMMAND_CLEAR_CHAT_HISTORY_DESCRIPTION) //
                .build();
    }

    @Override
    public String getCommandName() {
        return COMMAND_CLEAR_CHAT_HISTORY_NAME;
    }

    @Override
    public Mono<Void> handleCommand(final DiscordChatService discordChatService, final ChatInputInteractionEvent event,
                                    final String guildId) {
        discordChatService.resetChatHistory(guildId);
        log.info(LOG_PREFIX_DISCORD + "Chat history cleared in guild: {}", guildId);
        return event.reply(COMMAND_CLEAR_CHAT_HISTORY_SUCCESS);
    }

}