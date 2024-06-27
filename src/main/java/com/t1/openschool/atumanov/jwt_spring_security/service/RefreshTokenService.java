package com.t1.openschool.atumanov.jwt_spring_security.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.RefreshToken;
import com.t1.openschool.atumanov.jwt_spring_security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refreshTokenExpiration}")
    private Duration expiration;

    public RefreshToken save(String userId) {
        String refreshTokenValue = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();

        RefreshToken token = new RefreshToken(refreshTokenId, userId, refreshTokenValue);
        repository.save(token, expiration);
        return token;
    }

    public RefreshToken getByValue(String token) {
        return repository.getByValue(token);
    }
}
