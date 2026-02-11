package com.sidework.security.component;

import com.sidework.common.auth.AuthenticatedUser;
import com.sidework.common.auth.CurrentUserProvider;
import com.sidework.security.exception.UnauthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {
    @Override
    public AuthenticatedUser authenticatedUser() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof AuthenticatedUser)) {
            throw new UnauthenticatedException();
        }

        return (AuthenticatedUser) auth.getPrincipal();
    }
}
