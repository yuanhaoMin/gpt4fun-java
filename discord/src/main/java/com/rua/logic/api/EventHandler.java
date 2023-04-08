package com.rua.logic.api;

import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;


public interface EventHandler<T extends Event> {

    Logger logger = LoggerFactory.getLogger(EventHandler.class);

    Class<T> getEventType();

    Mono<Void> execute(T event);

    default Mono<Void> handleError(Throwable error) {
        logger.error("Discord -- Unable to process " + getEventType().getSimpleName(), error);
        return Mono.empty();
    }

}