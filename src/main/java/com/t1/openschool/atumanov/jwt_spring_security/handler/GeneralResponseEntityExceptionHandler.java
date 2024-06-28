package com.t1.openschool.atumanov.jwt_spring_security.handler;

import com.t1.openschool.atumanov.jwt_spring_security.exception.AuthFailedException;
import com.t1.openschool.atumanov.jwt_spring_security.model.Info;
import com.t1.openschool.atumanov.jwt_spring_security.model.exception.RefreshTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GeneralResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String DUPLICATE_EMAIL = "User with this email already exists";
    public static final String ACCESS_DENIED = "User doesn't have sufficient rights to access this resource";
    private static final String EXPIRED_TOKEN = "User's token has expired, receive new using refresh token";
    private static final String TOKEN_NOT_FOUND = "User's token is not found, please receive new token";

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    private ResponseEntity<Info> handleDuplicateEmail() {
        //return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
        return new ResponseEntity<>(new Info(DUPLICATE_EMAIL), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = AuthFailedException.class)
    private ResponseEntity<Info> handleAuthFailure(RuntimeException ex) {
        return new ResponseEntity<>(new Info(ex.getMessage()), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    private ResponseEntity<Info> handleAccessDenied() {
        return new ResponseEntity<>(new Info(ACCESS_DENIED), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    private ResponseEntity<Info> handleExpiredToken() {
        return new ResponseEntity<>(new Info(EXPIRED_TOKEN), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = RefreshTokenException.class)
    private ResponseEntity<Info> handleNotFoundToken() {
        return new ResponseEntity<>(new Info(TOKEN_NOT_FOUND), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }
}
