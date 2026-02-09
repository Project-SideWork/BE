package com.sidework.common.auth;

// 현재 사용자를 가져오는 방법
public interface CurrentUserProvider {
    AuthenticatedUser authenticatedUser();
}
