package com.pharm.pharmfinder.controller;

public class UsernameAlreadyTakenException extends IllegalArgumentException {
    public UsernameAlreadyTakenException() {
    }

    public UsernameAlreadyTakenException(String s) {
        super(s);
    }

    public UsernameAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyTakenException(Throwable cause) {
        super(cause);
    }
}
