package com.rua.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chamber_user_completion")
public class ChamberUserCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private ChamberUser user;

    @Nullable
    @Column(name = "message", length = 100000)
    private String message;

    @Column(name = "model")
    private String model;

    @Column(name = "temperature")
    private Double temperature;

}