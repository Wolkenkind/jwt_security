package com.t1.openschool.atumanov.jwt_spring_security.api.nonsecured;

import com.t1.openschool.atumanov.jwt_spring_security.controller.PublicApiDelegate;
import com.t1.openschool.atumanov.jwt_spring_security.exception.AuthFailedException;
import com.t1.openschool.atumanov.jwt_spring_security.model.*;
import com.t1.openschool.atumanov.jwt_spring_security.service.SecurityService;
import com.t1.openschool.atumanov.jwt_spring_security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicApiDelegateImpl implements PublicApiDelegate {

    public static final String RESPONSE_INFO = "PUBLIC INFORMATION: JSON Web Tokens are an open, " +
            "industry standard RFC 7519 method for representing claims securely between two parties.";
    public static final String AUTH_FAIL_INFO = "Failed to authenticate with provided username and password";
    public static final String REFRESH_FAIL_INFO = "Failed to refresh using provided token";

    private final UserService userService;
    private final SecurityService securityService;

    @Override
    public ResponseEntity<AuthenticateUser200Response> authenticateUser(AuthData authData) {
        return securityService.processPasswordToken(authData.getUsername(), authData.getPassword())
                .map(tokenData -> ResponseEntity.ok(new AuthenticateUser200Response(tokenData.getToken(), tokenData.getRefreshToken())))
                .orElseThrow(() -> new AuthFailedException(AUTH_FAIL_INFO));
    }

    @Override
    public ResponseEntity<Void> createUser(NewUser newUser) {
        User user = new User(newUser);
        user = userService.createUser(user);
        log.info("user '{}' with pass '{}' saved", user.getUsername(), user.getPassword());
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Info> getInfo() {
        return ResponseEntity.ok(new Info(RESPONSE_INFO));
    }

    @Override
    public ResponseEntity<AuthenticateUser200Response> refreshTokens(RefreshTokenData refreshTokenData) {
        return securityService.processRefreshToken(refreshTokenData.getRefreshToken())
                .map(tokenData -> ResponseEntity.ok(new AuthenticateUser200Response(tokenData.getToken(), tokenData.getRefreshToken())))
                .orElseThrow(() -> new AuthFailedException(REFRESH_FAIL_INFO));
    }
}
