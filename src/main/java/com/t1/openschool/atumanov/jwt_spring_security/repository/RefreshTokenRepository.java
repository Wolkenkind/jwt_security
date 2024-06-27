package com.t1.openschool.atumanov.jwt_spring_security.repository;

import com.t1.openschool.atumanov.jwt_spring_security.model.exception.RefreshTokenException;
import com.t1.openschool.atumanov.jwt_spring_security.model.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@Slf4j
public class RefreshTokenRepository {

    private static final String REFRESH_TOKEN_INDEX = "refreshTokenIndex";

    private final ValueOperations<String, RefreshToken> valueOperations;
    private final HashOperations<String, String, String> hashOperations;

    public RefreshTokenRepository(RedisTemplate<String, RefreshToken> refreshTokenRedisTemplate) {
        valueOperations = refreshTokenRedisTemplate.opsForValue();
        hashOperations = refreshTokenRedisTemplate.opsForHash();
    }

    public void save(RefreshToken token, Duration expirationTime) {
        valueOperations.set(token.getId(), token, expirationTime);
        hashOperations.put(REFRESH_TOKEN_INDEX, token.getValue(), token.getId());
    }

    public RefreshToken getByValue(String token) {
        String id = hashOperations.get(REFRESH_TOKEN_INDEX, token);
        if(id == null) {
            throw new RefreshTokenException("Refresh token not found: " + token);
        }
        log.info("Cleanup refreshToken hash count: {}", hashOperations.delete(REFRESH_TOKEN_INDEX, token));
        RefreshToken result = valueOperations.get(id);
        if(result == null) {
            throw new RefreshTokenException("Refresh token not found: " + token);
        } else {
            return result;
        }
    }
}
