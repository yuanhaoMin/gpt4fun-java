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
@Table(name = "chamber_user")
public class ChamberUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private ChamberUserChatCompletion userChatCompletion;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private ChamberUserCompletion userCompletion;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "access_bitmap", nullable = false)
    private Integer accessBitmap;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

}