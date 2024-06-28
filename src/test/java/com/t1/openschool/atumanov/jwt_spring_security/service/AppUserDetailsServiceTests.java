package com.t1.openschool.atumanov.jwt_spring_security.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.User;
import com.t1.openschool.atumanov.jwt_spring_security.repository.UserRepository;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class AppUserDetailsServiceTests {

    private static final String USERNAME = "user";
    private static final String USERPASS = "pa$$w0rd";
    private static final String USERMAIL = "mail@server.com";


    private static UserRepository userRepository;
    private static AppUserDetailsService appUserDetailsService;

    @BeforeAll
    static void setup() {
        userRepository = createUserRepository();
        appUserDetailsService = new AppUserDetailsService(userRepository);
    }

    private static UserRepository createUserRepository() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(USERPASS);
        user.setEmail(USERMAIL);
        user.setRoles(Stream.of(User.Role.USER).collect(Collectors.toSet()));

        UserRepository mock = Mockito.mock(UserRepository.class);
        when(mock.findByUsername(anyString())).thenReturn(Optional.empty());
        when(mock.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        return mock;
    }

    @Test
    void loadByUsernameTest() {
        UserDetails details = appUserDetailsService.loadUserByUsername(USERNAME);

        Assert.notNull(details);
        Assert.isTrue(details.isAccountNonExpired());
        Assert.isTrue(details.isEnabled());
        Assert.isTrue(details.isAccountNonLocked());
        Assert.isTrue(details.isCredentialsNonExpired());
        assert details.getUsername().equals(USERNAME);
        assert details.getPassword().equals(USERPASS);
        assert details.getAuthorities().equals(Stream.of(new SimpleGrantedAuthority(User.Role.USER.name())).collect(Collectors.toList()));

        Mockito.verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void loadNonExistentUserTest() {
        assertThrows(UsernameNotFoundException.class, () -> appUserDetailsService.loadUserByUsername("UNICORN"));
    }
}
