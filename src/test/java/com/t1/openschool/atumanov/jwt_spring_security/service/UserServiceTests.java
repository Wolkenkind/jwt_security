package com.t1.openschool.atumanov.jwt_spring_security.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.User;
import com.t1.openschool.atumanov.jwt_spring_security.repository.UserRepository;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private static final String USERNAME = "bob";
    private static final String USERPASS = "a33Le$";
    private static final String USERMAIL = "bob@arrested.com";
    private static final Long USERID = 1L;
    private static final Set<User.Role> USERROLES = Stream.of(User.Role.USER).collect(Collectors.toSet());

    private static UserService userService;
    private static User user;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeAll
    static void setup() {
        UserRepository userRepository = createUserRepository();
        userService = new UserService(userRepository, passwordEncoder);
    }

    private static UserRepository createUserRepository() {
        user = new User();
        user.setId(USERID);
        user.setUsername(USERNAME);
        user.setPassword(USERPASS);
        user.setEmail(USERMAIL);
        user.setRoles(USERROLES);

        UserRepository mock = Mockito.mock(UserRepository.class);
        when(mock.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mock.findByUsername(anyString())).thenReturn(Optional.empty());
        when(mock.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mock.findById(anyLong())).thenReturn(Optional.empty());
        when(mock.findById(USERID)).thenReturn(Optional.of(user));
        return mock;
    }

    @Test
    void createUserTest() {
        User result = userService.createUser(user);
        assert result.getId() == USERID;
        assert result.getUsername().equals(USERNAME);
        assert result.getEmail().equals(USERMAIL);
        assert result.getRoles().equals(USERROLES);
        assert passwordEncoder.matches(USERPASS, result.getPassword());
    }

    @Test
    void findByUsernameTest() {
        assert userService.findByUsername(USERNAME).isPresent();
    }

    @Test
    void findByIdTest() {
        assert userService.findById(USERID).isPresent();
    }

    @Test
    void findNonExistentByUsernameTest() {
        assert userService.findByUsername("JUSTICE").isEmpty();
    }

    @Test
    void findNonExistentByIdTest() {
        assert userService.findById(42L).isEmpty();
    }
}
