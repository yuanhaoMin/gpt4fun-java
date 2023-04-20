package com.rua.exception;

public class ChamberInvalidUserException extends IllegalArgumentException {

    public ChamberInvalidUserException(final String message) {
        super(message);
    }

}