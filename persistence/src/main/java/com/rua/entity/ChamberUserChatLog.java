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
@Table(name = "chamber_user_chat_log")
public class ChamberUserChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private ChamberUser user;

    @Column(name = "messages", length = 100000)
    private String messages;

    @Column(name = "last_chat_time")
    private LocalDateTime lastChatTime;

}