package com.rua.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_chat_log")
public class ChamberUserChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(name = "messages", length = 100000)
    private String messages;

    @Column(name = "last_chat_time")
    private String lastChatTime;

}