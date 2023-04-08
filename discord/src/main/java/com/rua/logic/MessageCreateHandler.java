package com.rua.logic;

import com.rua.logic.api.EventHandler;
import com.rua.model.DiscordCompleteChatRequestBo;
import com.rua.property.DiscordProperties;
import com.rua.service.DiscordChatService;
import com.rua.util.FormatUtils;
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

@Component
@RequiredArgsConstructor
public class MessageCreateHandler implements EventHandler<MessageCreateEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MessageCreateHandler.class);

    private final DiscordChatService discordChatService;

    private final DiscordProperties discordProperties;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(final MessageCreateEvent event) {
        final long startTime = System.currentTimeMillis();
        final var message = event.getMessage();
        final var isNotBot = message.getAuthor().map(user -> !user.isBot()).orElse(false);
        final var isMentioned = message.getUserMentionIds().contains(Snowflake.of(discordProperties.applicationId()));
        if (isNotBot && isMentioned) {
            final var guildId = message.getGuildId().map(Snowflake::asString).orElse("");
            final var userMessageContent = getMessageContentWithoutMention(message.getContent());
            final var userName = message.getAuthor().map(User::getUsername).orElse("");
            final var request = DiscordCompleteChatRequestBo.builder() //
                    .guildId(guildId) //
                    .maxCompletionTokens(discordProperties.maxCompletionTokens()) //
                    .maxPromptTokens(discordProperties.maxPromptTokens()) //
                    .userName(userName) //
                    .lastChatTime(LocalDateTime.now(Clock.system(ZoneId.of("Europe/Paris")))) //
                    .build();
            final var response = discordChatService.completeChat(request, userMessageContent);
            final long endTime = System.currentTimeMillis();
            logger.info("Discord -- Chat completed in {}s in guild: {}",
                    FormatUtils.convertMillisToStringWithMaxTwoFractionDigits(endTime - startTime), guildId);
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