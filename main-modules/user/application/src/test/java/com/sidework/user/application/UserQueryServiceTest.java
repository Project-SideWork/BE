package com.sidework.user.application;

import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.application.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {
    @Mock
    private UserOutPort repo;

    @InjectMocks
    private UserQueryService service;

    @Test
    void email로_중복_여부를_조회한다() {
        String email = "test1@naver.com";
        when(repo.existsByEmail(email)).thenReturn(true);

        boolean res = service.checkEmailExists(email);

        assertTrue(res);
        verify(repo).existsByEmail(email);
    }
}
