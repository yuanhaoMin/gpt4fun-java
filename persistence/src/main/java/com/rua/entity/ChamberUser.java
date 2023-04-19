package com.rua.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

}