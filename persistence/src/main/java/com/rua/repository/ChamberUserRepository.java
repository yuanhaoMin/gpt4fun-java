package com.rua.repository;

import com.rua.entity.ChamberUser;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChamberUserRepository extends JpaRepository<ChamberUser, Long> {

    @Nullable
    ChamberUser findByUsername(String username);

}