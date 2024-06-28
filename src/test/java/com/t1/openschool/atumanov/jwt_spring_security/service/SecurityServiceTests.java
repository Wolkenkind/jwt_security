package com.t1.openschool.atumanov.jwt_spring_security.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.RefreshToken;
import com.t1.openschool.atumanov.jwt_spring_security.model.User;
import com.t1.openschool.atumanov.jwt_spring_security.model.exception.AuthException;
import com.t1.openschool.atumanov.jwt_spring_security.model.exception.RefreshTokenException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class SecurityServiceTests {

    private static final String USERNAME = "alice";
    private static final String USERPASS = "d14M0nd*";
    private static final String USERMAIL = "alice@youth.com";
    private static final Long USERID = 13L;
    private static final Set<User.Role> USERROLES = Stream.of(User.Role.USER, User.Role.MODERATOR).collect(Collectors.toSet());
    private static final String TOKEN = "accesstoken";
    private static final String REFRESH_TOKEN_ID = "TOKEN_ID";
    private static final String REFRESH_TOKEN_VALUE = "TOKEN_VALUE";
    private static final String NON_EXISTENT_REFRESH_TOKEN_VALUE = "NE";
    private static final String NON_EXISTENT_USER_REFRESH_TOKEN_VALUE = "NEU";

    private static SecurityService securityService;
    private static User user;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeAll
    static void setup() {
        user = new User();
        user.setId(USERID);
        user.setUsername(USERNAME);
        user.setPassword(passwordEncoder.encode(USERPASS));
        user.setEmail(USERMAIL);
        user.setRoles(USERROLES);

        UserService userService = createUserService();
        TokenService tokenService = createTokenService();
        RefreshTokenService refreshTokenService = createRefreshTokenService();

        securityService = new SecurityService(userService, passwordEncoder, tokenService, refreshTokenService);
    }

    @Test
    void processPasswordTokenTest() {
        assert securityService.processPasswordToken(USERNAME, USERPASS).isPresent();
    }

    @Test
    void processRefreshTokenTest() {
        assert securityService.processRefreshToken(REFRESH_TOKEN_VALUE).isPresent();
    }

    @Test
    void processPasswordTokenForNonExistentUser() {
        assert securityService.processPasswordToken("RUHE", "STILL").isEmpty();
    }

    @Test
    void processPasswordTokenWithNotMatchingPassword() {
        assertThrows(AuthException.class, () -> securityService.processPasswordToken(USERNAME, "wrongpass"));
    }

    @Test
    void processNonExistentRefreshToken() {
        assertThrows(RefreshTokenException.class, () -> securityService.processRefreshToken(NON_EXISTENT_REFRESH_TOKEN_VALUE));
    }

    @Test
    void processRefreshTokenForNonExistentUser() {
        assert securityService.processRefreshToken(NON_EXISTENT_USER_REFRESH_TOKEN_VALUE).isEmpty();
    }

    private static RefreshTokenService createRefreshTokenService() {
        RefreshTokenService mock = Mockito.mock(RefreshTokenService.class);
        RefreshToken refreshToken = new RefreshToken(REFRESH_TOKEN_ID, USERID.toString(), REFRESH_TOKEN_VALUE);
        RefreshToken noUserRefreshToken = new RefreshToken("?", "666", "?");

        when(mock.save(USERID.toString())).thenReturn(refreshToken);
        when(mock.getByValue(REFRESH_TOKEN_VALUE)).thenReturn(refreshToken);
        when(mock.getByValue(NON_EXISTENT_REFRESH_TOKEN_VALUE)).thenThrow(RefreshTokenException.class);
        when(mock.getByValue(NON_EXISTENT_USER_REFRESH_TOKEN_VALUE)).thenReturn(noUserRefreshToken);

        return mock;
    }

    private static TokenService createTokenService() {
        TokenService mock = Mockito.mock(TokenService.class);

        when(mock.generateToken(USERNAME, USERID, USERROLES.stream().map(User.Role::name).toList())).thenReturn(TOKEN);

        return mock;
    }

    private static UserService createUserService() {
        UserService mock = Mockito.mock(UserService.class);

        when(mock.findByUsername(anyString())).thenReturn(Optional.empty());
        when(mock.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mock.findById(anyLong())).thenReturn(Optional.empty());
        when(mock.findById(USERID)).thenReturn(Optional.of(user));

        return mock;
    }
}
