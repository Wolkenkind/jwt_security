package com.t1.openschool.atumanov.jwt_spring_security.model.exception;

public class AuthException extends RuntimeException {
    public AuthException() {

    }

    public AuthException(String message) {
        super(message);
    }
}
