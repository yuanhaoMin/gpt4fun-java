package com.rua.logic;

import com.rua.logic.api.DiscordEventHandler;
import com.rua.model.DiscordCompleteChatRequest;
import com.rua.property.DiscordProperties;
import com.rua.service.DiscordChatService;
import com.rua.util.SharedFormatUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.rua.constant.DiscordConstants.LOG_PREFIX_DISCORD;

@Component
@RequiredArgsConstructor
public class DiscordMessageCreateHandler implements DiscordEventHandler<MessageCreateEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DiscordMessageCreateHandler.class);

    private final DiscordChatService discordChatService;

    private final DiscordProperties discordProperties;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(final MessageCreateEvent event) {
        final var startTime = System.currentTimeMillis();
        final var message = event.getMessage();
        final boolean isNotBot = message.getAuthor().map(user -> !user.isBot()).orElse(false);
        final var isMentioned = message.getUserMentionIds().contains(Snowflake.of(discordProperties.applicationId()));
        if (isNotBot && isMentioned) {
            final var guildId = message.getGuildId().map(Snowflake::asString).orElse("");
            final var userMessage = getMessageContentWithoutMention(message.getContent());
            final var userName = message.getAuthor().map(User::getUsername).orElse("");
            final var request = DiscordCompleteChatRequest.builder() //
                    .guildId(guildId) //
                    .lastChatTime(LocalDateTime.now(Clock.system(ZoneId.of("Europe/Paris")))) //
                    .userName(userName) //
                    .userMessage(userMessage) //
                    .build();
            final var response = discordChatService.gpt35completeChat(request);
            final var endTime = System.currentTimeMillis();
            final var executionTime = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(
                    endTime - startTime);
            logger.info(LOG_PREFIX_DISCORD + "Message created in {}s in guild: {}", executionTime, guildId);
            return message.getChannel().flatMap(channel -> channel.createMessage(response).then());
        } else {
            return Mono.empty();
        }
    }

    // Mentions can have patterns like <@!1234567890>, <@&1234567890> or <@1234567890>
    private String getMessageContentWithoutMention(final String messageContent) {
        final var start = messageContent.indexOf("<@");
        if (start != -1) {
            final var end = messageContent.indexOf(">", start);
            if (end != -1) {
                return messageContent.substring(end + 1).trim();
            }
        }
        return messageContent;
    }

}