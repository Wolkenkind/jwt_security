package com.t1.openschool.atumanov.jwt_spring_security.handler;

import com.t1.openschool.atumanov.jwt_spring_security.model.Info;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GeneralResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    protected ResponseEntity<Info> handleDuplicateEmail(
                RuntimeException ex, WebRequest request) {
        String body = "User with this email already exists";
        //return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
        return new ResponseEntity<>(new Info(body), new HttpHeaders(), HttpStatus.CONFLICT);
    }
}
