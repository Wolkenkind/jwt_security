package com.t1.openschool.atumanov.jwt_spring_security.controller;

import com.t1.openschool.atumanov.jwt_spring_security.api.nonsecured.PublicApiDelegateImpl;
import com.t1.openschool.atumanov.jwt_spring_security.api.secured.UserApiDelegateImpl;
import com.t1.openschool.atumanov.jwt_spring_security.handler.GeneralResponseEntityExceptionHandler;
import com.t1.openschool.atumanov.jwt_spring_security.model.*;
import com.t1.openschool.atumanov.jwt_spring_security.model.dto.TokenData;
import com.t1.openschool.atumanov.jwt_spring_security.repository.RefreshTokenRepository;
import com.t1.openschool.atumanov.jwt_spring_security.repository.UserRepository;
import com.t1.openschool.atumanov.jwt_spring_security.service.SecurityService;
import com.t1.openschool.atumanov.jwt_spring_security.service.TokenService;
import com.t1.openschool.atumanov.jwt_spring_security.service.UserService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiDelegateImplTests {
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pas$$w0rD";
    private static final String EMAIL = "mail@server.com";
    private static final Long USERID = 3L;
    private static final List<String> ROLES = Stream.of(Role.USER.name(), Role.MODERATOR.name()).toList();
    private static final String TOKEN = "token";
    private static final String REFRESH_TOKEN_VALUE = "rtoken";

    @MockBean
    UserService userService;
    @MockBean
    SecurityService securityService;
    @MockBean
    RefreshTokenRepository refreshTokenRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean(reset = MockReset.NONE)
    TokenService tokenService;


    private static String apiUrl;

    @LocalServerPort
    private int serverTestPort;
    @Autowired
    private TestRestTemplate template;

    @Test
    @Order(1)
    void contextLoads() {
        apiUrl = "http://localhost:" + serverTestPort + "/user/info";

        //when(userService.createUser(any())).thenReturn(new User(new NewUser(USERNAME, PASSWORD, EMAIL, ROLES)));

        Principal principal = new AppUserPrincipal(USERID.toString(), USERNAME, ROLES);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                ROLES.stream().map(SimpleGrantedAuthority::new).toList());

        when(tokenService.generateToken(USERNAME, USERID, ROLES)).thenReturn(TOKEN);
        when(tokenService.toAuthentication(TOKEN)).thenReturn(auth);

        Assert.notNull(template);

//        when(securityService.processPasswordToken(USERNAME, PASSWORD)).thenReturn(Optional.of(new TokenData(TOKEN, REFRESH_TOKEN_VALUE)));
//        when(securityService.processRefreshToken(REFRESH_TOKEN_VALUE)).thenReturn(Optional.of(new TokenData(TOKEN, REFRESH_TOKEN_VALUE)));
    }

    @Test
    @Order(2)
    void getUserInfoAuthenticatedTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(TOKEN);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Info> responseEntity = template.exchange(apiUrl, HttpMethod.GET, entity, Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert responseEntity.getBody().getInformation().equals("Hello, user '" + USERNAME + "'! " + UserApiDelegateImpl.USER_INFO);
    }

    @Test
    @Order(2)
    void getUserInfoNotAuthenticatedTest() {
        ResponseEntity<Info> responseEntity = template.getForEntity(apiUrl, Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.FORBIDDEN);
        //Http403ForbiddenEntryPoint
        //TODO: replace AccessDeniedHandler? add test for role, add test for duplicate user?
        assert responseEntity.getBody().getInformation().equals(GeneralResponseEntityExceptionHandler.ACCESS_DENIED);
    }
}
