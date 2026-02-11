package com.sidework.common.auth;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SecurityAuthenticatedUser implements AuthenticatedUser {

    private final Long id;
    private final String email;
    private final String name;

    @Override public Long getId() { return id; }
    @Override public String getEmail() { return email; }
    @Override public String getName() { return name; }
}