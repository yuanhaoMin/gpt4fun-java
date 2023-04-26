package com.rua.logic;

import com.rua.ChamberUserPrincipal;
import com.rua.entity.ChamberUser;
import com.rua.entity.ChamberUserChatCompletion;
import com.rua.entity.ChamberUserCompletion;
import com.rua.exception.ChamberInvalidUserException;
import com.rua.repository.ChamberUserRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.rua.constant.ChamberConstants.LOG_PREFIX_TIME_CHAMBER;
import static com.rua.util.SharedFormatUtils.getCurrentTimeInParis;

@Component
@RequiredArgsConstructor
public class ChamberUserLogic {

    private final ChamberUserRepository chamberUserRepository;

    public ChamberUserPrincipal authenticateUser(@Nonnull final String username, @Nonnull final String password) {
        final var user = chamberUserRepository.findByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            throw new ChamberInvalidUserException(LOG_PREFIX_TIME_CHAMBER + "Invalid username or password");
        }
        user.setLastLoginTime(getCurrentTimeInParis());
        chamberUserRepository.save(user);
        return new ChamberUserPrincipal(user);
    }

    public void createUser(@Nonnull final String username, @Nonnull final String password) {
        final var existingUser = chamberUserRepository.findByUsername(username);
        if (existingUser != null) {
            throw new ChamberInvalidUserException(LOG_PREFIX_TIME_CHAMBER + "The username already exists");
        }
        final var userToSave = ChamberUser.builder() //
                .createdTime(getCurrentTimeInParis()) //
                .username(username) //
                .password(password) //
                .build();
        final var chamberUserChatCompletion = new ChamberUserChatCompletion();
        chamberUserChatCompletion.setUser(userToSave);
        userToSave.setUserChatCompletion(chamberUserChatCompletion);
        final var chamberUserCompletion = new ChamberUserCompletion();
        chamberUserCompletion.setUser(userToSave);
        userToSave.setUserCompletion(chamberUserCompletion);
        // Save the user
        chamberUserRepository.save(userToSave);
    }

    // Nonnull because otherwise the authentication will fail before the controller method is called
    @Nonnull
    public ChamberUser findByUsername(final String username) throws UsernameNotFoundException {
        final var user = chamberUserRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

}