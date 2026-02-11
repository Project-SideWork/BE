package com.sidework.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {
    private final StringRedisTemplate redisTemplate;

    public void addRefreshTokenBlackList(String refreshToken, long expiration){
        String logoutTime = String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForValue().set("blacklist:refresh:" + refreshToken, logoutTime, Duration.ofSeconds(expiration));
    }

    public boolean isAlreadyBlackListed(String refreshToken){
        Optional<String> refreshOpt = Optional.ofNullable(redisTemplate.opsForValue().get("blacklist:refresh:" + refreshToken));
        return refreshOpt.isPresent();
    }
}