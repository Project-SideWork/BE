package com.sidework.security.service;

import com.sidework.common.auth.AuthenticatedUser;
import com.sidework.common.auth.SecurityAuthenticatedUser;
import com.sidework.security.dto.AuthenticatedUserDetails;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedUserDetailsService implements UserDetailsService {
    private final UserOutPort repo;
    @Override
    public AuthenticatedUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email);
        AuthenticatedUser authenticatedUser = new SecurityAuthenticatedUser(
                user.getId(), user.getEmail(), user.getName()
        );

        return new AuthenticatedUserDetails(authenticatedUser.getId(),
                authenticatedUser.getEmail(), authenticatedUser.getName(), user.getPassword());
    }
}
