package com.sidework.user.application;

import com.sidework.user.application.adapter.SignUpCommand;
import com.sidework.user.application.exception.InvalidCommandException;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.application.service.UserCommandService;
import com.sidework.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class UserCommandServiceTest {
    @Mock
    private UserOutPort repo;

    @InjectMocks
    private UserCommandService service;

    @Test
    void 정상적인_회원가입_요청_DTO로_회원가입에_성공한다() {
        SignUpCommand command = createCommand();
        service.signUp(command);
        verify(repo).save(any(User.class));
    }

    @Test
    void 일부_값이_누락된_회원가입_요청_DTO로_회원가입에_실패한다() {
        SignUpCommand command = createInvalidCommand();
        assertThrows(InvalidCommandException.class,
                () -> service.signUp(command));
    }

    private SignUpCommand createCommand(){
        return new SignUpCommand(
                "test@test.com",
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678"
        );
    }

    private SignUpCommand createInvalidCommand(){
        return new SignUpCommand(
                null,
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678"
        );
    }
}
