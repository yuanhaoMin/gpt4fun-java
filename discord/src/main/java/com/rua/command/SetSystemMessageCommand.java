package com.rua.command;

import com.rua.command.api.CommandHandler;
import com.rua.command.api.CommandRequestBuilder;
import com.rua.constant.DiscordConstants;
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

@Component
public class SetSystemMessageCommand extends CommandHandler implements CommandRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SetSystemMessageCommand.class);

    @Override
    public ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder() //
                .name(DiscordConstants.SET_SYSTEM_MESSAGE_COMMAND_NAME) //
                .description(DiscordConstants.SET_SYSTEM_MESSAGE_COMMAND_DESCRIPTION) //
                .addOption(ApplicationCommandOptionData.builder() //
                        .name(DiscordConstants.SET_SYSTEM_MESSAGE_COMMAND_FIRST_OPTION_NAME) //
                        .description(DiscordConstants.SET_SYSTEM_MESSAGE_COMMAND_FIRST_OPTION_DESCRIPTION) //
                        .type(ApplicationCommandOption.Type.STRING.getValue()) //
                        .required(true) //
                        .build()) //
                .build();
    }

    @Override
    public String getCommandName() {
        return DiscordConstants.SET_SYSTEM_MESSAGE_COMMAND_NAME;
    }

    @Override
    public Mono<Void> handleCommand(final DiscordChatService discordChatService, final ChatInputInteractionEvent event,
                                    final String guildId) {
        final var optInteraction = event.getInteraction().getCommandInteraction();
        if (optInteraction.isEmpty()) {
            return Mono.empty();
        }
        final var systemMessageContent = optInteraction.get() //
                .getOption(DiscordConstants.SET_SYSTEM_MESSAGE_COMMAND_FIRST_OPTION_NAME) //
                .flatMap(ApplicationCommandInteractionOption::getValue) //
                .map(ApplicationCommandInteractionOptionValue::asString) //
                .orElse("");
        discordChatService.updateSystemMessage(guildId, systemMessageContent);
        logger.info("Discord -- System message updated in guild: {}", guildId);
        return event.reply(String.format(DiscordConstants.SET_SYSTEM_MESSAGE_COMMAND_SUCCESS, systemMessageContent));
    }

}