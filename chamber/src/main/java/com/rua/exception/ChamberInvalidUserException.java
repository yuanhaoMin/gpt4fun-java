package com.rua.exception;

import org.springframework.security.core.AuthenticationException;

public class ChamberInvalidUserException extends AuthenticationException {

    public ChamberInvalidUserException(final String message) {
        super(message);
    }

}