package com.sidework.security.service;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedUserDetailsService implements UserDetailsService {
    private final UserOutPort repo;
    @Override
    public AuthenticatedUserDetails loadUserByUsername(String email) {
            User user = repo.findByEmail(email);
            return new AuthenticatedUserDetails(
                    user.getId(), user.getEmail(), user.getName(), user.getPassword());
    }
}
