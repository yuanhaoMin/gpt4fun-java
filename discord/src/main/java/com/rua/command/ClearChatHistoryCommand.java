package com.rua.command;

import com.rua.command.api.CommandHandler;
import com.rua.command.api.CommandRequestBuilder;
import com.rua.constant.DiscordConstants;
import com.rua.service.DiscordChatService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class ClearChatHistoryCommand extends CommandHandler implements CommandRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ClearChatHistoryCommand.class);

    @Override
    public ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder() //
                .name(DiscordConstants.CLEAR_CHAT_HISTORY_COMMAND_NAME) //
                .description(DiscordConstants.CLEAR_CHAT_HISTORY_COMMAND_DESCRIPTION) //
                .build();
    }

    @Override
    public String getCommandName() {
        return DiscordConstants.CLEAR_CHAT_HISTORY_COMMAND_NAME;
    }

    @Override
    public Mono<Void> handleCommand(final DiscordChatService discordChatService, final ChatInputInteractionEvent event,
                                    final String guildId) {
        discordChatService.resetChatHistory(guildId);
        logger.info("Discord -- Chat history cleared in guild: {}", guildId);
        return event.reply(DiscordConstants.CLEAR_CHAT_HISTORY_COMMAND_SUCCESS);
    }

}