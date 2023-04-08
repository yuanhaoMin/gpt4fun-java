package com.rua.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "guild_chat_logs")
public class DiscordGuildChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "guild_id", unique = true)
    private String guildId;

    @Column(name = "messages", length = 100000)
    private String messages;

    @Column(name = "last_chat_time")
    private LocalDateTime lastChatTime;

    @Column(name = "last_chat_user_name")
    private String lastChatUserName;

}