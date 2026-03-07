package com.sidework.user.application;

import com.sidework.region.application.exception.InvalidRegionLevelException;
import com.sidework.region.application.exception.RegionNotFoundException;
import com.sidework.region.application.port.out.RegionOutPort;
import com.sidework.user.application.exception.DuplicatedInformationException;
import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.application.service.UserCommandService;
import com.sidework.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCommandServiceTest {
    @Mock
    private RegionOutPort regionRepo;

    @Mock
    private UserOutPort repo;

    @InjectMocks
    private UserCommandService service;

    @Spy
    private BCryptPasswordEncoder encoder;


    @Test
    void 정상적인_회원가입_요청_DTO로_회원가입에_성공한다() {
        SignUpCommand command = createCommand();
        when(regionRepo.existsById(command.residenceRegionId())).thenReturn(true);
        when(regionRepo.checkIsSubRegion(command.residenceRegionId())).thenReturn(true);
        when(repo.existsByEmail(command.email())).thenReturn(false);
        when(repo.existsByNickname(command.nickname())).thenReturn(false);
        when(repo.existsByTel(command.tel())).thenReturn(false);

        service.signUp(command);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());

        User savedUser = captor.getValue();
        assertTrue(encoder.matches(command.password(), savedUser.getPassword()));
        assertNotEquals(command.password(), savedUser.getPassword());
        verify(encoder).encode(command.password());
    }

    @Test
    void 회원가입_요청_DTO에_포함된_거주지역ID가_존재하지_않으면_RegionNotFoundException을_던진다() {
        SignUpCommand command = createCommand();
        when(regionRepo.existsById(command.residenceRegionId())).thenReturn(false);
        assertThrows(
                RegionNotFoundException.class,
                () -> service.signUp(command)
        );
    }

    @Test
    void 회원가입_요청_DTO에_포함된_거주지역ID가_하위행정구역이_아니면_InvalidRegionLevelException을_던진다() {
        SignUpCommand command = createCommand();
        when(regionRepo.existsById(command.residenceRegionId())).thenReturn(true);
        when(regionRepo.checkIsSubRegion(command.residenceRegionId())).thenReturn(false);
        assertThrows(
                InvalidRegionLevelException.class,
                () -> service.signUp(command)
        );
    }

    @Test
    void 회원가입_요청_DTO에_포함된_이메일이_중복되면_DuplicatedInformationException을_던진다() {
        SignUpCommand command = createCommand();
        when(regionRepo.existsById(command.residenceRegionId())).thenReturn(true);
        when(regionRepo.checkIsSubRegion(command.residenceRegionId())).thenReturn(true);
        when(repo.existsByEmail(command.email())).thenReturn(true);
        assertThrows(
                DuplicatedInformationException.class,
                () -> service.signUp(command)
        );
    }

    @Test
    void 회원가입_요청_DTO에_포함된_닉네임이_중복되면_DuplicatedInformationException을_던진다() {
        SignUpCommand command = createCommand();
        when(regionRepo.existsById(command.residenceRegionId())).thenReturn(true);
        when(regionRepo.checkIsSubRegion(command.residenceRegionId())).thenReturn(true);
        when(repo.existsByEmail(command.email())).thenReturn(false);
        when(repo.existsByNickname(command.nickname())).thenReturn(true);
        assertThrows(
                DuplicatedInformationException.class,
                () -> service.signUp(command)
        );
    }

    @Test
    void 회원가입_요청_DTO에_포함된_전화번호가_중복되면_DuplicatedInformationException을_던진다() {
        SignUpCommand command = createCommand();
        when(regionRepo.existsById(command.residenceRegionId())).thenReturn(true);
        when(regionRepo.checkIsSubRegion(command.residenceRegionId())).thenReturn(true);
        when(repo.existsByEmail(command.email())).thenReturn(false);
        when(repo.existsByNickname(command.nickname())).thenReturn(false);
        when(repo.existsByTel(command.tel())).thenReturn(true);
        assertThrows(
                DuplicatedInformationException.class,
                () -> service.signUp(command)
        );
    }

    private SignUpCommand createCommand(){
        return new SignUpCommand(
                "test@test.com",
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678",
                1L
        );
    }

    private SignUpCommand createInvalidCommand(){
        return new SignUpCommand(
                null,
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678",
                1L
        );
    }
}
