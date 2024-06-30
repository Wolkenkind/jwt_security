package com.t1.openschool.atumanov.jwt_spring_security.api.secured;

import com.t1.openschool.atumanov.jwt_spring_security.controller.AdminApiDelegate;
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
public class AdminApiDelegateImpl implements AdminApiDelegate {
    public static final String ADMIN_INFO = "This is info with access level 'ADMIN'. You're like a god now...";

    private final NativeWebRequest nativeWebRequest;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(nativeWebRequest);
    }

    @Override
    public ResponseEntity<Info> getAdminInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(new Info("Hello, user '" + auth.getName() + "'! " + ADMIN_INFO));
    }
}
