package com.t1.openschool.atumanov.jwt_spring_security.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    public enum Role {
        USER, MODERATOR, ADMIN

        /*USER(Constants.USER_VALUE), MODERATOR(Constants.MODERATOR_VALUE), ADMIN(Constants.ADMIN_VALUE);
        private final String stringValue;
        private Role(String stringValue) {
            this.stringValue = stringValue;
        }

        public static class Constants {
            public static final String USER_VALUE = "USER";
            public static final String MODERATOR_VALUE = "MODERATOR";
            public static final String ADMIN_VALUE = "ADMIN";
        }*/
    }

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    @ElementCollection(targetClass = User.Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    public User(NewUser user) {
        username = user.getUsername();
        email = user.getEmail();
        password = user.getPassword();
        roles = user.getRoles().stream().map(role -> User.Role.valueOf(role.getValue())).collect(Collectors.toSet());
    }
}
