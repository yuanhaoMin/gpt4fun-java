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

    // No cascade type is specified here because we don't want to modify the user when we modify the chat log
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private ChamberUser user;

    @Column(name = "messages", length = 100000)
    private String messages;

    @Column(name = "last_chat_time")
    private LocalDateTime lastChatTime;

//    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    private ChamberUserChatLog userChatLog;

}