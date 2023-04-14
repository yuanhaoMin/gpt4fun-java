package com.rua.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "guild_chat_log")
public class DiscordGuildChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guild_id", unique = true)
    private String guildId;

    @Column(name = "messages", length = 100000)
    private String messages;

    @Column(name = "last_chat_time")
    private String lastChatTime;

    @Column(name = "last_chat_user_name")
    private String lastChatUserName;

}