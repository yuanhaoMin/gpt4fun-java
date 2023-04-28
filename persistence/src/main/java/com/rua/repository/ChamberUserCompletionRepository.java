package com.rua.repository;

import com.rua.entity.ChamberUserCompletion;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChamberUserCompletionRepository extends JpaRepository<ChamberUserCompletion, Long> {

    @Nullable
    ChamberUserCompletion findByUserId(Long userId);

}