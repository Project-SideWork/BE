package com.sidework.user.persistence;

import com.sidework.user.application.adapter.SignUpCommand;
import com.sidework.user.domain.User;
import com.sidework.user.domain.UserType;
import com.sidework.user.persistence.adapter.UserPersistenceAdapter;
import com.sidework.user.persistence.entity.UserEntity;
import com.sidework.user.persistence.mapper.UserMapper;
import com.sidework.user.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPersistenceAdapterTest {
    private final UserJpaRepository repo = mock(UserJpaRepository.class);
    private final UserMapper mapper = mock(UserMapper.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);

    private final UserPersistenceAdapter adapter =             new UserPersistenceAdapter(repo, mapper);


    @Test
    void 회원가입에_필요한_값을_모두_입력하면_성공한다() {
        // given
        SignUpCommand validCommand = createCommand();
        User domain = createUser();
        UserEntity entity = createUserEntity(null);
        UserEntity saved = createUserEntity(1L);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(repo.findById(1L)).thenReturn(Optional.of(saved));
        when(mapper.toDomain(saved)).thenReturn(domain);

        // when
        adapter.save(domain);
        User user = adapter.findById(1L);

        // then
        assertNotNull(user);
        assertEquals(1L, user.getId());

        verify(repo, times(1)).save(entity);
        verify(mapper, times(1)).toEntity(domain);
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void 회원가입에_필요한_값_하나라도_누락되면_실패한다() {

    }

    @Test
    void DB에_존재하지_않는_이메일로_중복_확인시_false를_반환한다() {}


    @Test
    void DB에_존재하는_이메일로_중복_확인시_true를_반환한다() {}

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

    private User createUser(){
        return new User(
                "test@test.com",
                "테스트",
                "테스트1",
                "password123!",
                20,
                "010-1234-5678",
                UserType.LOCAL
        );
    }

    private UserEntity createUserEntity(Long id){
        return new UserEntity(
                id,
                "test@test.com",
                "테스트",
                "테스트1",
                "password123!",
                20,
                "010-1234-5678",
                UserType.LOCAL,
                true
        );
    }
}

