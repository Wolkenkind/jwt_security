package com.t1.openschool.atumanov.jwt_spring_security.test.api;

import com.t1.openschool.atumanov.jwt_spring_security.api.nonsecured.ApiErrorController;
import com.t1.openschool.atumanov.jwt_spring_security.api.secured.ModeratorApiDelegateImpl;
import com.t1.openschool.atumanov.jwt_spring_security.handler.GeneralResponseEntityExceptionHandler;
import com.t1.openschool.atumanov.jwt_spring_security.model.AppUserPrincipal;
import com.t1.openschool.atumanov.jwt_spring_security.model.Info;
import com.t1.openschool.atumanov.jwt_spring_security.model.Role;
import com.t1.openschool.atumanov.jwt_spring_security.repository.RefreshTokenRepository;
import com.t1.openschool.atumanov.jwt_spring_security.repository.UserRepository;
import com.t1.openschool.atumanov.jwt_spring_security.service.SecurityService;
import com.t1.openschool.atumanov.jwt_spring_security.service.TokenService;
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
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {TestConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModeratorApiDelegateImplTests {
    private static final String USERNAME = "user";
    private static final Long USERID = 3L;
    private static final List<String> ROLES = Stream.of(Role.MODERATOR.name()).toList();
    private static final String TOKEN = "token";
    private static final String TOKEN_NO_RIGHTS = "nortoken";

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
        apiUrl = "http://localhost:" + serverTestPort + "/moderator/info";

        Principal principal = new AppUserPrincipal(USERID.toString(), USERNAME, ROLES);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                ROLES.stream().map(SimpleGrantedAuthority::new).toList());
        UsernamePasswordAuthenticationToken authNoRights = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                new ArrayList<>());

        when(tokenService.toAuthentication(TOKEN)).thenReturn(auth);
        when(tokenService.toAuthentication(TOKEN_NO_RIGHTS)).thenReturn(authNoRights);

        Assert.notNull(template);
    }

    @Test
    @Order(2)
    void getUserInfoAuthenticatedTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(TOKEN);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Info> responseEntity = template.exchange(apiUrl, HttpMethod.GET, entity, Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert Objects.requireNonNull(responseEntity.getBody()).getInformation().equals("Hello, user '" + USERNAME + "'! " + ModeratorApiDelegateImpl.MODERATOR_INFO);
    }

    @Test
    @Order(2)
    void getUserInfoNotAuthenticatedTest() {
        ResponseEntity<Info> responseEntity = template.getForEntity(apiUrl, Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.FORBIDDEN);
        assert Objects.requireNonNull(responseEntity.getBody()).getInformation().equals(ApiErrorController.FORBIDDEN);
    }

    @Test
    @Order(2)
    void getUserInfoAuthenticatedInsufficientRightsTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(TOKEN_NO_RIGHTS);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Info> responseEntity = template.exchange(apiUrl, HttpMethod.GET, entity, Info.class);

        assert responseEntity.getStatusCode().equals(HttpStatus.FORBIDDEN);
        assert Objects.requireNonNull(responseEntity.getBody()).getInformation().equals(GeneralResponseEntityExceptionHandler.ACCESS_DENIED);
    }
}
