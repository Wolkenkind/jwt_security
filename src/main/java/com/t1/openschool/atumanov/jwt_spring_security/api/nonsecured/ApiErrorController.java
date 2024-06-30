package com.t1.openschool.atumanov.jwt_spring_security.api.nonsecured;

import com.t1.openschool.atumanov.jwt_spring_security.model.Info;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.t1.openschool.atumanov.jwt_spring_security.handler.GeneralResponseEntityExceptionHandler.ACCESS_DENIED;

@Controller
public class ApiErrorController implements ErrorController {

    public static final String FORBIDDEN = "Access restricted";

    @RequestMapping("/error")
    public ResponseEntity<Info> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Integer statusCode = null;
        if (status != null) {
            statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return new ResponseEntity<>(new Info(FORBIDDEN), new HttpHeaders(), HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<>(HttpStatus.valueOf(statusCode));
    }

}
