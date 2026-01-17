package com.sidework.user.persistence;

import com.sidework.user.application.adapter.SignUpCommand;
import com.sidework.user.application.exception.InvalidCommandException;
import com.sidework.user.domain.User;
import com.sidework.user.domain.UserType;
import com.sidework.user.persistence.adapter.UserPersistenceAdapter;
import com.sidework.user.persistence.entity.UserEntity;
import com.sidework.user.persistence.exception.UserNotFoundException;
import com.sidework.user.persistence.mapper.UserMapper;
import com.sidework.user.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPersistenceAdapterTest {
    @Mock
    private UserJpaRepository repo;

    UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private UserPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserPersistenceAdapter(repo, mapper);
    }

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        User domain = createUser(createCommand());
        adapter.save(domain);
        verify(repo).save(any(UserEntity.class));
    }

    @Test
    void existByEmail은_이메일_중복_여부를_확인한다() {
        String emailExists = "test1@naver.com";
        String emailNotExists = "test2@naver.com";
        when(repo.existsByEmail(emailExists)).thenReturn(true);
        when(repo.existsByEmail(emailNotExists)).thenReturn(false);

        boolean exists = adapter.existsByEmail(emailExists);
        boolean notExists = adapter.existsByEmail(emailNotExists);

        assertTrue(exists);
        assertFalse(notExists);

        verify(repo).existsByEmail(emailExists);
        verify(repo).existsByEmail(emailNotExists);
    }

    @Test
    void findById는_Id로_사용자를_조회해_도메인_객체로_변환한다() {
        UserEntity entity = createUserEntity(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        User user = adapter.findById(1L);

        assertNotNull(user);
        assertEquals(1L, user.getId());

        verify(repo).findById(1L);
    }

    @Test
    void findById로_존재하지_않는_사용자_조회_시_UserNotFoundException을_던진다() {
        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> adapter.findById(2L));
        verify(repo).findById(2L);
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

    private User createUser(SignUpCommand command){
        return User.create(command.email(),
                command.name(),
                command.nickname(),
                command.password(),
                command.age(),
                command.tel(),
                UserType.LOCAL);
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

