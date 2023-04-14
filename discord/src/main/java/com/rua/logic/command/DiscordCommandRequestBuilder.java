package com.rua.logic.command;

import discord4j.discordjson.json.ApplicationCommandRequest;

public interface DiscordCommandRequestBuilder {

    ApplicationCommandRequest build();

}