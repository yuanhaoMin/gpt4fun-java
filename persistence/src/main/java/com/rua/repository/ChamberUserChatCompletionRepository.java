package com.rua.repository;

import com.rua.entity.ChamberUserChatCompletion;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChamberUserChatCompletionRepository extends JpaRepository<ChamberUserChatCompletion, Long> {

    @Nullable
    ChamberUserChatCompletion findByUserId(Long userId);

}