package com.sidework.user.application.service;

import com.sidework.region.application.exception.RegionNotFoundException;
import com.sidework.region.application.port.out.RegionOutPort;
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
        User user = User.create(command.email(), command.name(), command.nickname(), encodePassword(command.password())
                , command.age(), command.tel(), command.residenceRegionId(), UserType.LOCAL);
        if(regionRepository.existsById(command.residenceRegionId())) {
            userRepository.save(user);
        } else {
            throw new RegionNotFoundException(command.residenceRegionId());
        }
    }

    private String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
