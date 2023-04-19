package com.rua.repository;

import com.rua.entity.ChamberUserChatLog;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChamberUserChatLogRepository extends JpaRepository<ChamberUserChatLog, Long> {

    @Nullable
    ChamberUserChatLog findByUserId(Long userId);

}