package com.t1.openschool.atumanov.jwt_spring_security.service;

import com.t1.openschool.atumanov.jwt_spring_security.model.AppUserDetails;
import com.t1.openschool.atumanov.jwt_spring_security.model.User;
import com.t1.openschool.atumanov.jwt_spring_security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public Optional<AppUserDetails> findByUsername(String username) {
        return repository.findByUsername(username).map(AppUserDetails::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(username);
        if(user.isPresent()) {
            return new AppUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }
    }
}
