package com.t1.openschool.atumanov.jwt_spring_security.api.secured;

import com.t1.openschool.atumanov.jwt_spring_security.controller.UserApiDelegate;
import com.t1.openschool.atumanov.jwt_spring_security.model.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserApiDelegateImpl implements UserApiDelegate {
    private static final String USER_INFO = "This is info with access level 'USER'";

    private final NativeWebRequest nativeWebRequest;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(nativeWebRequest);
    }

    @Override
    //@PreAuthorize("hasRole('" + User.Role.Constants.USER_VALUE + "')")
    //@PreAuthorize("hasRole('ADMIN')") - doesn't work here, needs to be at interface level
    public ResponseEntity<Info> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(new Info("Hello, user '" + auth.getName() + "'! " + USER_INFO));
    }
}
