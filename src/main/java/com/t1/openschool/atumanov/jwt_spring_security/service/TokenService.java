package com.t1.openschool.atumanov.jwt_spring_security.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.AppUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TokenService {

    private static final String ROLE_CLAIM = "role";
    private static final String ID_CLAIM = "id";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refreshTokenExpiration}")
    private Duration tokenExpiration;

    public String generateToken(String username, Long id, List<String> roles) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpiration.toMillis()))
                .claim(ROLE_CLAIM, roles)
                .claim(ID_CLAIM, id)
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();
    }

    public UsernamePasswordAuthenticationToken toAuthentication(String token) {
        Claims tokenBody = Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(token).getBody();
        String subject = tokenBody.getSubject();
        Long id = tokenBody.get(ID_CLAIM, Long.class);
        Date expirationDate = tokenBody.getExpiration();

        List<String> roles = (List<String>) tokenBody.get(ROLE_CLAIM);

        Principal principal = new AppUserPrincipal(id.toString(), subject, roles);

        return expirationDate.before(new Date()) ? null :
                new UsernamePasswordAuthenticationToken(
                principal,
                null,
                roles.stream().map(SimpleGrantedAuthority::new).toList());
    }

//    public boolean isTokenExpired(String token) {
//        return Jwts.parserBuilder().setSigningKey(jwtSecret).build()
//                .parseClaimsJws(token).getBody().getExpiration()
//                .before(new Date());
//    }
}
