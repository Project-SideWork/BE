package com.sidework.user.application.service;

import com.sidework.common.response.status.ErrorStatus;
import com.sidework.region.application.exception.InvalidRegionLevelException;
import com.sidework.region.application.exception.RegionNotFoundException;
import com.sidework.region.application.port.out.RegionOutPort;
import com.sidework.user.application.exception.DuplicatedInformationException;
import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.port.in.UserCommandUseCase;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.domain.User;
import com.sidework.user.domain.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class UserCommandService implements UserCommandUseCase {
    private final RegionOutPort regionRepository;
    private final UserOutPort userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void signUp(SignUpCommand command) {
        Long residenceRegionId = command.residenceRegionId();;
        checkRegionValidation(residenceRegionId);
        checkCommandInfoValidation(command.email(), command.nickname(), command.tel());
        User user = User.create(command.email(), command.name(), command.nickname(), encodePassword(command.password())
                , command.age(), command.tel(), residenceRegionId, UserType.LOCAL);

        userRepository.save(user);
    }

    @Override
    public void updateMe(Long userId, String email, String name, String nickname, Integer age, String tel, Long residenceRegionId) {
        User current = userRepository.findById(userId);
        checkUpdateInfoValidation(userId, current, email, nickname, tel, residenceRegionId);
        current.update(email, name, nickname, age, tel, residenceRegionId);
        userRepository.save(current);
    }

    private void checkUpdateInfoValidation(Long userId, User current, String email, String nickname, String tel, Long residenceRegionId) {
        if (residenceRegionId != null) {
            checkRegionValidation(residenceRegionId);
        }
        if (email != null && !email.equals(current.getEmail())) {
            if (userRepository.existsByEmailExcludingUserId(email, userId)) {
                throw new DuplicatedInformationException(ErrorStatus.EMAIL_ALREADY_EXISTS);
            }
        }
        if (nickname != null && !nickname.equals(current.getNickname())) {
            if (userRepository.existsByNicknameExcludingUserId(nickname, userId)) {
                throw new DuplicatedInformationException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
            }
        }
        if (tel != null && !tel.equals(current.getTel())) {
            if (userRepository.existsByTelExcludingUserId(tel, userId)) {
                throw new DuplicatedInformationException(ErrorStatus.TEL_ALREADY_EXISTS);
            }
        }
    }

    private String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    private void checkRegionValidation(Long residenceRegionId) {
        if(!regionRepository.existsById(residenceRegionId)) {
            throw new RegionNotFoundException(residenceRegionId);
        } else {
            if(!regionRepository.checkIsSubRegion(residenceRegionId)) {
                throw new InvalidRegionLevelException();
            }
        }
    }

    private void checkCommandInfoValidation(String email, String nickname, String tel) {
        if(userRepository.existsByEmail(email)) throw new DuplicatedInformationException(ErrorStatus.EMAIL_ALREADY_EXISTS);
        if(userRepository.existsByNickname(nickname)) throw new DuplicatedInformationException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
        if(userRepository.existsByTel(tel)) throw new DuplicatedInformationException(ErrorStatus.TEL_ALREADY_EXISTS);
    }
}
