package com.rua.logic;

import com.rua.entity.ChamberUser;
import com.rua.repository.ChamberUserRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.rua.constant.ChamberConstants.LOG_PREFIX_TIME_CHAMBER;

@Component
@RequiredArgsConstructor
public class ChamberUserLogic {

    private final ChamberUserRepository chamberUserRepository;

    // Happens during authentication phase, before controller logic is executed
    @Nonnull
    public ChamberUser findUserByUsername(final String username) throws UsernameNotFoundException {
        final var user = chamberUserRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(LOG_PREFIX_TIME_CHAMBER + "User not found");
        }
        return user;
    }

}