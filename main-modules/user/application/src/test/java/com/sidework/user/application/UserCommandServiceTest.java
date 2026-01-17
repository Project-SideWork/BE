package com.sidework.user.application;

import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.exception.InvalidCommandException;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.application.service.UserCommandService;
import com.sidework.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserCommandServiceTest {
    @Mock
    private UserOutPort repo;

    @InjectMocks
    private UserCommandService service;

    @Mock
    private BCryptPasswordEncoder encoder;


    @Test
    void 정상적인_회원가입_요청_DTO로_회원가입에_성공한다() {
        SignUpCommand command = createCommand();
        service.signUp(command);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(savedUser.getPassword(), encoder.encode(command.password()));
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
