package com.rua.logic.handler;

import com.rua.logic.DiscordChatLogic;
import com.rua.logic.DiscordPostConstructGateWayClient;
import com.rua.model.request.DiscordCompleteChatRequestBo;
import com.rua.property.DiscordProperties;
import com.rua.service.DiscordChatService;
import com.rua.util.SharedFormatUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.rua.constant.DiscordConstants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordMessageCreateHandler implements DiscordEventHandler<MessageCreateEvent> {

    private final DiscordChatLogic discordChatLogic;

    private final DiscordChatService discordChatService;

    private final DiscordProperties discordProperties;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    /**
     * This method is presented in an imperative style and lacks a signal for the invocation of
     * {@link DiscordChatService#gpt35completeChat(DiscordCompleteChatRequestBo)}. As a result, any errors that may occur
     * will not be caught by the onErrorResume function in {@link DiscordPostConstructGateWayClient#init()}.
     * Therefore, a traditional try-catch block is used to catch and log any errors that may occur.
     */
    @Override
    public Mono<Void> execute(final MessageCreateEvent event) {
        final var startTime = System.currentTimeMillis();
        final var message = event.getMessage();
        final boolean isNotBot = message.getAuthor().map(user -> !user.isBot()).orElse(false);
        final boolean isMentioned = message.getUserMentionIds()
                .contains(Snowflake.of(discordProperties.applicationId()));
        if (isNotBot && isMentioned) {
            final var guildId = message.getGuildId().map(Snowflake::asString).orElse("");
            final var userMessage = getMessageContentWithoutMention(message.getContent());
            final var userName = message.getAuthor().map(User::getUsername).orElse("");
            final var request = DiscordCompleteChatRequestBo.builder() //
                    .guildId(guildId) //
                    .lastChatTime(LocalDateTime.now(Clock.system(ZoneId.of("Europe/Paris")))) //
                    .userName(userName) //
                    .userMessage(userMessage) //
                    .build();
            try {
                final var botResponse = discordChatService.gpt35completeChat(request);
                final var endTime = System.currentTimeMillis();
                final var executionTime = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(
                        endTime - startTime);
                log.info(LOG_PREFIX_DISCORD + "GPT3.5 chat completed in {}s in guild: {}", executionTime, guildId);
                return sendMessageToChannel(message, botResponse);
            } catch (FeignException.BadRequest e) {
                final var errorLog = e.toString();
                log.error(LOG_PREFIX_DISCORD + "Unable to complete GPT3.5 chat due to bad request: {}", errorLog);
                discordChatLogic.resetChatHistory(guildId);
                return sendMessageToChannel(message, GPT_35_CHAT_BAD_REQUEST);
            } catch (RetryableException e) {
                final var errorLog = e.toString();
                log.error(LOG_PREFIX_DISCORD + "Unable to complete GPT3.5 chat due to feign retryable error: {}",
                        errorLog);
                return sendMessageToChannel(message, GPT_35_CHAT_READ_TIME_OUT);
            } catch (Exception e) {
                final var errorLog = e.toString();
                log.error(LOG_PREFIX_DISCORD + "Unable to complete GPT3.5 chat with error: {}", errorLog);
                return Mono.empty();
            }
        }
        return Mono.empty();
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

    private Mono<Void> sendMessageToChannel(final Message message, final String botResponse) {
        return message.getChannel() //
                .flatMap(channel -> channel.createMessage(botResponse) //
                        .onErrorResume(e -> {
                            final var errorLog = e.toString();
                            log.error(LOG_PREFIX_DISCORD + "Unable to send message, error: {}", errorLog);
                            return Mono.empty();
                        }) //
                        .then());
    }

}