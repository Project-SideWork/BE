package com.sidework.common.auth;

// 현재 사용자가 가진 정보를 정의하는 인터페이스
public interface AuthenticatedUser {
    Long getId();
    String getEmail();
    String getName();
}
