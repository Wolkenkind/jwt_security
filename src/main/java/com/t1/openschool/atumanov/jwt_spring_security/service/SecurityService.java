package com.t1.openschool.atumanov.jwt_spring_security.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.exception.AuthException;
import com.t1.openschool.atumanov.jwt_spring_security.model.RefreshToken;
import com.t1.openschool.atumanov.jwt_spring_security.model.User;
import com.t1.openschool.atumanov.jwt_spring_security.model.dto.TokenData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    public Optional<TokenData> processPasswordToken(String username, String password) {
        Optional<User> user = userService.findByUsername(username);
        if(user.isEmpty()) {
            return Optional.empty();
        }
        if(!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new AuthException("Exception trying to check password for user: " + username);
        }
        return Optional.of(createTokenData(user.get()));
    }

    public Optional<TokenData> processRefreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.getByValue(refreshTokenValue);
        Optional<User> user = userService.findById(Long.valueOf(refreshToken.getUserId()));
        return Optional.ofNullable(user.map(this::createTokenData).orElse(null));
    }

    private TokenData createTokenData(User user) {
        String token = tokenService.generateToken(
                user.getUsername(),
                user.getId(),
                user.getRoles().stream().map(Enum::name).toList());

        return new TokenData(token, refreshTokenService.save(user.getId().toString()).getValue());
    }
}
