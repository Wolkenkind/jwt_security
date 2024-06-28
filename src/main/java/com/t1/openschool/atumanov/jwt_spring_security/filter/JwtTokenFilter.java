package com.t1.openschool.atumanov.jwt_spring_security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.openschool.atumanov.jwt_spring_security.model.Info;
import com.t1.openschool.atumanov.jwt_spring_security.service.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String JWT_SECURITY_PROBLEM = "User's token is malformed, corrupted or has been tampered with";

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if(authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            token = authHeader.substring(BEARER_PREFIX.length());

            try{
                UsernamePasswordAuthenticationToken auth = tokenService.toAuthentication(token);

                if(auth != null) {
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException ex) {
                raiseException(response, JWT_SECURITY_PROBLEM);
                return;
            }/* catch (RuntimeException ex) {
                raiseException(response, ex.getMessage());
                return;
            }*/
        }

        filterChain.doFilter(request, response);
    }

    private void raiseException(HttpServletResponse response, String info) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if(info != null) {
            byte[] body = new ObjectMapper().writeValueAsBytes(new Info(info));
            response.getOutputStream().write(body);
        }
    }
}
