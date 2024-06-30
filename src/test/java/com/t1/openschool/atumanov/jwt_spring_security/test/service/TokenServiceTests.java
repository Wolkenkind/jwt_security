package com.t1.openschool.atumanov.jwt_spring_security.test.service;

import com.t1.openschool.atumanov.jwt_spring_security.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Stream;


@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@ContextConfiguration(classes = TokenService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenServiceTests {
    private static final String USERNAME = "user";
    private static final String USERNAME2 = "user2";
    private static final Long ID = 1L;
    private static final Long ID2 = 11L;
    private static final List<String> ROLES = Stream.of("USER", "ADMIN").toList();
    private static final List<String> ROLES2 = Stream.of("MODERATOR").toList();
    private static final String ID_CLAIM = "id";

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String key;

    @Test
    @Order(1)
    void contextLoads() {
        Assert.notNull(tokenService);
    }

    @Test
    @Order(2)
    void getAuthenticationTest() {
        String token = tokenService.generateToken(USERNAME, ID, ROLES);

        Authentication auth = tokenService.toAuthentication(token);
        Claims tokenBody = Jwts.parserBuilder().setSigningKey(key.getBytes()).build().parseClaimsJws(token).getBody();
        assert auth.getName().equals(USERNAME);
        assert auth.getAuthorities().equals(ROLES.stream().map(SimpleGrantedAuthority::new).toList());
        assert tokenBody.get(ID_CLAIM, Long.class) == 1L;
    }

    @Test
    @Order(2)
    void generateTokenTest() {
        String token1 = tokenService.generateToken(USERNAME, ID, ROLES);
        String token2 = tokenService.generateToken(USERNAME2, ID2, ROLES2);
        assert !token1.equals(token2);
    }
}
