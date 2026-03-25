package com.sidework.user.application;

import com.sidework.common.exception.InvalidCommandException;
import com.sidework.common.util.AesEncryptor;
import com.sidework.user.application.exception.GithubInfoNotFoundException;
import com.sidework.user.application.exception.UserNotFoundException;
import com.sidework.user.application.port.in.GithubInfoResponse;
import com.sidework.user.application.port.out.GithubInfoDto;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.application.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {
    @Mock
    private UserOutPort repo;

    @Mock
    private AesEncryptor encryptor;

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

    @Test
    void queryGithubToken는_깃허브Id와_복호화된_액세스_토큰을_조회한다() {
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);
        when(repo.findGithubInfoProjection(id)).thenReturn(new GithubInfoDto(1L, "accesstoken"));
        when(encryptor.decrypt("accesstoken")).thenReturn("decoded");

        GithubInfoResponse res = service.queryGithubToken(id);

        assertNotEquals("accesstoken", res.rawGithubToken());
        assertEquals(1L, res.githubId());

        verify(repo).existsById(id);
        verify(repo).findGithubInfoProjection(id);
    }

    @Test
    void queryGithubToken는_전달받은_id가_null이면_InvalidCommandException을_던진다() {
        Long id = null;
        assertThrows(
                InvalidCommandException.class,
                () -> service.queryGithubToken(id)
        );
    }

    @Test
    void queryGithubToken는_조회한_결과_중_하나라도_null이면_GithubInfoNotFoundException을_던진다() {
        Long first = 1L;
        Long second = 2L;
        when(repo.findGithubInfoProjection(first)).thenReturn(new GithubInfoDto(1L, null));
        when(repo.findGithubInfoProjection(second)).thenReturn(new GithubInfoDto(null, "accesstoken"));

        assertThrows(
                GithubInfoNotFoundException.class,
                () -> service.queryGithubToken(first)
        );

        assertThrows(
                GithubInfoNotFoundException.class,
                () -> service.queryGithubToken(second)
        );
    }

    @Test
    void queryGithubToken에_전달된_id가_존재하지_않는_사용자의_것이면_UserNotFoundException을_던진다() {
        when(repo.existsById(1L)).thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> service.queryGithubToken(1L)
        );

        verify(repo).existsById(1L);
    }
}
