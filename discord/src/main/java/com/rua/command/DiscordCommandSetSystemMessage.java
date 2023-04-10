package com.rua.command;

import com.rua.command.api.DiscordCommandHandler;
import com.rua.command.api.DiscordCommandRequestBuilder;
import com.rua.service.DiscordChatService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.rua.constant.DiscordConstants.*;

@Component
public class DiscordCommandSetSystemMessage implements DiscordCommandHandler, DiscordCommandRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DiscordCommandSetSystemMessage.class);

    @Override
    public ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder() //
                .name(COMMAND_SET_SYSTEM_MESSAGE_NAME) //
                .description(COMMAND_SET_SYSTEM_MESSAGE_DESCRIPTION) //
                .addOption(ApplicationCommandOptionData.builder() //
                        .name(COMMAND_SET_SYSTEM_MESSAGE_FIRST_OPTION_NAME) //
                        .description(COMMAND_SET_SYSTEM_MESSAGE_FIRST_OPTION_DESCRIPTION) //
                        .type(ApplicationCommandOption.Type.STRING.getValue()) //
                        .required(true) //
                        .build()) //
                .build();
    }

    @Override
    public String getCommandName() {
        return COMMAND_SET_SYSTEM_MESSAGE_NAME;
    }

    @Override
    public Mono<Void> handleCommand(final DiscordChatService discordChatService, final ChatInputInteractionEvent event,
                                    final String guildId) {
        final var optInteraction = event.getInteraction().getCommandInteraction();
        if (optInteraction.isEmpty()) {
            return Mono.empty();
        }
        final var systemMessageContent = optInteraction.get() //
                .getOption(COMMAND_SET_SYSTEM_MESSAGE_FIRST_OPTION_NAME) //
                .flatMap(ApplicationCommandInteractionOption::getValue) //
                .map(ApplicationCommandInteractionOptionValue::asString) //
                .orElse("");
        discordChatService.updateSystemMessageAndPersist(guildId, systemMessageContent);
        logger.info(LOG_PREFIX_DISCORD + "System message updated in guild: {}", guildId);
        return event.reply(String.format(COMMAND_SET_SYSTEM_MESSAGE_SUCCESS, systemMessageContent));
    }

}