package com.rua.exception;

public class ChamberConflictUsernameException extends RuntimeException {

    public ChamberConflictUsernameException(final String message) {
        super(message);
    }

}