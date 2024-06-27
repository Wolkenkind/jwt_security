package com.t1.openschool.atumanov.jwt_spring_security.repository;

import com.t1.openschool.atumanov.jwt_spring_security.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}
