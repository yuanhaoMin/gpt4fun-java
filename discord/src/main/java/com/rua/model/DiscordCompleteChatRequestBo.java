package com.rua.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DiscordCompleteChatRequestBo extends CompleteChatRequestBo {

    private final String guildId;

    private final LocalDateTime lastChatTime;

    private final String userName;

}