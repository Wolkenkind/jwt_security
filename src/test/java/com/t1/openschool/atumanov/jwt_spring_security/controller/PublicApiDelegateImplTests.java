package com.t1.openschool.atumanov.jwt_spring_security.controller;

import com.t1.openschool.atumanov.jwt_spring_security.api.nonsecured.PublicApiDelegateImpl;
import com.t1.openschool.atumanov.jwt_spring_security.model.*;
import com.t1.openschool.atumanov.jwt_spring_security.model.dto.TokenData;
import com.t1.openschool.atumanov.jwt_spring_security.repository.RefreshTokenRepository;
import com.t1.openschool.atumanov.jwt_spring_security.repository.UserRepository;
import com.t1.openschool.atumanov.jwt_spring_security.service.SecurityService;
import com.t1.openschool.atumanov.jwt_spring_security.service.UserService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
/*@SpringBootApplication(
        exclude = {//RedisConfiguration.class,
                RedisAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class})*/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublicApiDelegateImplTests {

    private static final String USERNAME = "user";
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


    private static String baseApiUrl, infoUrl, newUserUrl, authUrl, refreshUrl;

    @LocalServerPort
    private int serverTestPort;
    @Autowired
    private TestRestTemplate template;

    @Test
    @Order(1)
    void contextLoads() {
        baseApiUrl = "http://localhost:" + serverTestPort + "/";

        infoUrl = baseApiUrl + "public/info";
        newUserUrl = baseApiUrl + "public/newuser";
        authUrl = baseApiUrl + "public/token/auth";
        refreshUrl = baseApiUrl + "public/token/refresh";

        when(userService.createUser(any())).thenReturn(new User(new NewUser(USERNAME, PASSWORD, EMAIL, ROLES)));

        when(securityService.processPasswordToken(USERNAME, PASSWORD)).thenReturn(Optional.of(new TokenData(TOKEN, REFRESH_TOKEN_VALUE)));
        when(securityService.processRefreshToken(REFRESH_TOKEN_VALUE)).thenReturn(Optional.of(new TokenData(TOKEN, REFRESH_TOKEN_VALUE)));

        Assert.notNull(template);
    }

    @Test
    void getInfoTest() {
        ResponseEntity<Info> responseEntity = template.getForEntity(infoUrl, Info.class);
        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert responseEntity.getBody().getInformation().equals(PublicApiDelegateImpl.RESPONSE_INFO);
    }

    @Test
    void createUserTest() {
        ResponseEntity<Void> responseEntity = template.postForEntity(newUserUrl, new NewUser(USERNAME, PASSWORD, EMAIL, ROLES), Void.class);
        assert responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED);
    }

    @Test
    void authenticateUserSuccessTest() {
        ResponseEntity<AuthenticateUser200Response> responseEntity = template.postForEntity(authUrl,
                                                                                            new AuthData(USERNAME, PASSWORD),
                                                                                            AuthenticateUser200Response.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert responseEntity.getBody().getToken().equals(TOKEN);
        assert responseEntity.getBody().getRefreshToken().equals(REFRESH_TOKEN_VALUE);
    }

    @Test
    void authenticateUserFailTest() {
        ResponseEntity<Info> responseEntity = template.postForEntity(authUrl, new AuthData("MRNOBODY", PASSWORD), Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.FORBIDDEN);
        assert responseEntity.getBody().getInformation().equals(PublicApiDelegateImpl.AUTH_FAIL_INFO);
    }

    @Test
    void refreshTokensSuccessTest() {
        ResponseEntity<AuthenticateUser200Response> responseEntity = template.postForEntity(refreshUrl,
                                                                                            new RefreshTokenData(REFRESH_TOKEN_VALUE),
                                                                                            AuthenticateUser200Response.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert responseEntity.getBody().getToken().equals(TOKEN);
        assert responseEntity.getBody().getRefreshToken().equals(REFRESH_TOKEN_VALUE);
    }

    @Test
    void refreshTokensFailTest() {
        ResponseEntity<Info> responseEntity = template.postForEntity(refreshUrl, new RefreshTokenData("NOT_A_REFRESH_TOKEN"), Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.FORBIDDEN);
        assert responseEntity.getBody().getInformation().equals(PublicApiDelegateImpl.REFRESH_FAIL_INFO);
    }
}
