package com.t1.openschool.atumanov.jwt_spring_security.test.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.RefreshToken;
import com.t1.openschool.atumanov.jwt_spring_security.model.exception.RefreshTokenException;
import com.t1.openschool.atumanov.jwt_spring_security.repository.RefreshTokenRepository;
import com.t1.openschool.atumanov.jwt_spring_security.service.RefreshTokenService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTests {

    private static final String TOKEN_ID = "tokenId";
    private static final String USER_ID = "userId";
    private static final String TOKEN_VALUE = "tokenValue";

    private static RefreshTokenService refreshTokenService;

    @BeforeAll
    static void setup() {
        RefreshTokenRepository refreshTokenRepository = createRefreshTokenRepository();
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
    }

    static RefreshTokenRepository createRefreshTokenRepository() {
        RefreshToken token = new RefreshToken(TOKEN_ID, USER_ID, TOKEN_VALUE);

        RefreshTokenRepository mock = Mockito.mock(RefreshTokenRepository.class);

        when(mock.getByValue(anyString())).thenAnswer(invocation -> {
            if(invocation.getArgument(0).equals(TOKEN_VALUE)) {
                return token;
            } else {
                throw new RefreshTokenException();
            }
        });

        return mock;
    }

    @Test
    void saveTest() {
        RefreshToken result = refreshTokenService.save(USER_ID);

        Assert.notNull(result);
        assert result.getUserId().equals(USER_ID);
        Assert.hasText(result.getId());
        Assert.hasText(result.getValue());
    }

    @Test
    void getByValueTest() {
        RefreshToken result = refreshTokenService.getByValue(TOKEN_VALUE);

        Assert.notNull(result);
        assert result.getUserId().equals(USER_ID);
        assert result.getId().equals(TOKEN_ID);
        assert result.getValue().equals(TOKEN_VALUE);
    }

    @Test
    void getNonExistentByValueTest() {
        assertThrows(RefreshTokenException.class, () -> refreshTokenService.getByValue("UNICORN"));
    }
}
