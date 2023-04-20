package com.rua.logic;

import com.rua.ChamberUserPrincipal;
import com.rua.entity.ChamberUser;
import com.rua.exception.ChamberInvalidUserException;
import com.rua.repository.ChamberUserRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.rua.constant.ChamberConstants.LOG_PREFIX_TIME_CHAMBER;

@Component
@RequiredArgsConstructor
public class ChamberUserLogic {

    private final ChamberUserRepository chamberUserRepository;

    private final PasswordEncoder passwordEncoder;

    // Happens during authentication phase, before controller logic is executed
    @Nonnull
    public ChamberUser findByUsername(final String username) throws UsernameNotFoundException {
        final var user = chamberUserRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(LOG_PREFIX_TIME_CHAMBER + "User not found");
        }
        return user;
    }

    public void createUser(@Nonnull final String username, @Nonnull final String password) {
        final var existingUser = chamberUserRepository.findByUsername(username);
        if (existingUser != null) {
            throw new ChamberInvalidUserException(LOG_PREFIX_TIME_CHAMBER + "The username already exists");
        }
        final var userToSave = ChamberUser.builder() //
                .username(username) //
                .password(passwordEncoder.encode(password)) //
                .build();
        chamberUserRepository.save(userToSave);
    }

    public ChamberUserPrincipal authenticateUser(@Nonnull final String username, @Nonnull final String password) {
        final var user = chamberUserRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ChamberInvalidUserException(LOG_PREFIX_TIME_CHAMBER + "Invalid username or password");
        }
        return new ChamberUserPrincipal(user);
    }

}