package com.sidework;

import com.sidework.security.service.TokenBlackListService;
import com.sidework.security.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TokenBlackListServiceTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlackListService service;

    @Test
    void 리프레쉬_토큰을_블랙리스트에_추가한다() {
        String refreshToken = "mock-refresh-token";
        long expiration = 3600L;

        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        service.addRefreshTokenBlackList(refreshToken, expiration);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        verify(valueOperations).set(
                keyCaptor.capture(),
                valueCaptor.capture(),
                durationCaptor.capture()
        );

        assertThat(keyCaptor.getValue()).isEqualTo("blacklist:refresh:" + refreshToken);
        assertThat(valueCaptor.getValue()).isNotEmpty(); // 시간 값이 있는지만 확인
        assertThat(durationCaptor.getValue()).isEqualTo(Duration.ofSeconds(expiration));
    }

    @Test
    void 블랙리스트에_등록된_토큰은_true를_반환한다() {
        String refreshToken = "mock-refresh-token";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("blacklist:refresh:" + refreshToken))
                .willReturn(refreshToken);

        boolean isBlackListed = service.isAlreadyBlackListed(refreshToken);

        assertTrue(isBlackListed);
    }

    @Test
    void 블랙리스트에_등록되지_않은_토큰은_false를_반환한다() {
        String refreshToken = "mock-refresh-token";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("blacklist:refresh:" + refreshToken))
                .willReturn(null);

        boolean isBlackListed = service.isAlreadyBlackListed(refreshToken);

        assertFalse(isBlackListed);
    }
}

