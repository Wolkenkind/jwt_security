package com.t1.openschool.atumanov.jwt_spring_security.test.api;

import com.t1.openschool.atumanov.jwt_spring_security.api.nonsecured.PublicApiDelegateImpl;
import com.t1.openschool.atumanov.jwt_spring_security.handler.GeneralResponseEntityExceptionHandler;
import com.t1.openschool.atumanov.jwt_spring_security.model.*;
import com.t1.openschool.atumanov.jwt_spring_security.model.dto.TokenData;
import com.t1.openschool.atumanov.jwt_spring_security.repository.RefreshTokenRepository;
import com.t1.openschool.atumanov.jwt_spring_security.repository.UserRepository;
import com.t1.openschool.atumanov.jwt_spring_security.service.SecurityService;
import com.t1.openschool.atumanov.jwt_spring_security.service.UserService;
import com.t1.openschool.atumanov.jwt_spring_security.test.TestConfiguration;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublicApiDelegateImplTests {

    private static final String USERNAME = "user";
    private static final String ANOTHER_USERNAME = "user2";
    private static final String PASSWORD = "pas$$w0rD";
    private static final String EMAIL = "mail@server.com";
    private static final List<Role> ROLES = Stream.of(Role.USER, Role.MODERATOR).toList();
    private static final String TOKEN = "token";
    private static final String REFRESH_TOKEN_VALUE = "rtoken";

    @MockBean(reset = MockReset.NONE)
    UserService userService;
    @MockBean(reset = MockReset.NONE)
    SecurityService securityService;
    @MockBean
    RefreshTokenRepository refreshTokenRepository;
    @MockBean
    UserRepository userRepository;


    private static String infoUrl, newUserUrl, authUrl, refreshUrl;
    private static User user, existingUser;

    @LocalServerPort
    private int serverTestPort;
    @Autowired
    private TestRestTemplate template;

    @Test
    @Order(1)
    void contextLoads() {
        String baseApiUrl = "http://localhost:" + serverTestPort + "/";

        infoUrl = baseApiUrl + "public/info";
        newUserUrl = baseApiUrl + "public/newuser";
        authUrl = baseApiUrl + "public/token/auth";
        refreshUrl = baseApiUrl + "public/token/refresh";
        user = new User(new NewUser(USERNAME, PASSWORD, EMAIL, ROLES));
        existingUser = new User(new NewUser(ANOTHER_USERNAME, PASSWORD, EMAIL, ROLES));

        when(userService.createUser(user)).thenReturn(user);
        when(userService.createUser(existingUser)).thenThrow(new DataIntegrityViolationException("duplicate key value"));

        when(securityService.processPasswordToken(USERNAME, PASSWORD)).thenReturn(Optional.of(new TokenData(TOKEN, REFRESH_TOKEN_VALUE)));
        when(securityService.processRefreshToken(REFRESH_TOKEN_VALUE)).thenReturn(Optional.of(new TokenData(TOKEN, REFRESH_TOKEN_VALUE)));

        Assert.notNull(template);
    }

    @Test
    void getInfoTest() {
        ResponseEntity<Info> responseEntity = template.getForEntity(infoUrl, Info.class);
        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert Objects.requireNonNull(responseEntity.getBody()).getInformation().equals(PublicApiDelegateImpl.RESPONSE_INFO);
    }

    @Test
    void createUserTest() {
        ResponseEntity<Void> responseEntity = template.postForEntity(newUserUrl, user, Void.class);
        assert responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED);
    }

    @Test
    void createDuplicateUserTest() {
        ResponseEntity<Info> responseEntity = template.postForEntity(newUserUrl, existingUser, Info.class);
        assert responseEntity.getStatusCode().equals(HttpStatus.CONFLICT);
        assert Objects.requireNonNull(responseEntity.getBody()).getInformation().equals(GeneralResponseEntityExceptionHandler.DUPLICATE_EMAIL);
    }

    @Test
    void authenticateUserSuccessTest() {
        ResponseEntity<AuthenticateUser200Response> responseEntity = template.postForEntity(authUrl,
                                                                                            new AuthData(USERNAME, PASSWORD),
                                                                                            AuthenticateUser200Response.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert Objects.requireNonNull(responseEntity.getBody()).getToken().equals(TOKEN);
        assert responseEntity.getBody().getRefreshToken().equals(REFRESH_TOKEN_VALUE);
    }

    @Test
    void authenticateUserFailTest() {
        ResponseEntity<Info> responseEntity = template.postForEntity(authUrl, new AuthData("MRNOBODY", PASSWORD), Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.FORBIDDEN);
        assert Objects.requireNonNull(responseEntity.getBody()).getInformation().equals(PublicApiDelegateImpl.AUTH_FAIL_INFO);
    }

    @Test
    void refreshTokensSuccessTest() {
        ResponseEntity<AuthenticateUser200Response> responseEntity = template.postForEntity(refreshUrl,
                                                                                            new RefreshTokenData(REFRESH_TOKEN_VALUE),
                                                                                            AuthenticateUser200Response.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert Objects.requireNonNull(responseEntity.getBody()).getToken().equals(TOKEN);
        assert responseEntity.getBody().getRefreshToken().equals(REFRESH_TOKEN_VALUE);
    }

    @Test
    void refreshTokensFailTest() {
        ResponseEntity<Info> responseEntity = template.postForEntity(refreshUrl, new RefreshTokenData("NOT_A_REFRESH_TOKEN"), Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.FORBIDDEN);
        assert Objects.requireNonNull(responseEntity.getBody()).getInformation().equals(PublicApiDelegateImpl.REFRESH_FAIL_INFO);
    }
}
