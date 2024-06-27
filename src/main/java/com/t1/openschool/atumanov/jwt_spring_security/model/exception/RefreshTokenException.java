package com.t1.openschool.atumanov.jwt_spring_security.model.exception;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException() {

    }

    public RefreshTokenException(String message) {
        super(message);
    }
}
