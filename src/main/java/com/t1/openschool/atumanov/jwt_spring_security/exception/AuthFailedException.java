package com.t1.openschool.atumanov.jwt_spring_security.exception;

public class AuthFailedException extends RuntimeException {
    public AuthFailedException() {

    }

    public AuthFailedException(String message) {
        super(message);
    }
}
