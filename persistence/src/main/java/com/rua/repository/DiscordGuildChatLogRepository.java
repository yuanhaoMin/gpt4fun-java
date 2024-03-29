package com.rua.repository;

import com.rua.entity.DiscordGuildChatLog;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordGuildChatLogRepository extends JpaRepository<DiscordGuildChatLog, Long> {

    @Nullable
    DiscordGuildChatLog findByGuildId(String guildId);

}