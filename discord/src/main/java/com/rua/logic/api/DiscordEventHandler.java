package com.rua.logic.api;

import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static com.rua.constant.DiscordConstants.LOG_PREFIX_DISCORD;


public interface DiscordEventHandler<T extends Event> {

    Logger logger = LoggerFactory.getLogger(DiscordEventHandler.class);

    Class<T> getEventType();

    Mono<Void> execute(T event);

    default Mono<Void> handleError(Throwable error) {
        final var errorLog = error.toString();
        logger.error(LOG_PREFIX_DISCORD + "Unable to process {}, error: {}", getEventType().getSimpleName(), errorLog);
        return Mono.error(error);
    }

}