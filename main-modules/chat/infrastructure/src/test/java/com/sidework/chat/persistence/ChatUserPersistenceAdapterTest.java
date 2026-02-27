package com.sidework.chat.persistence;

import com.sidework.chat.persistence.adapter.ChatUserPersistenceAdapter;
import com.sidework.chat.persistence.entity.ChatUserEntity;
import com.sidework.chat.persistence.mapper.ChatUserMapper;
import com.sidework.chat.persistence.repository.ChatUserJpaRepository;
import com.sidework.domain.ChatUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatUserPersistenceAdapterTest {
    @Mock
    private ChatUserJpaRepository repo;

    @Mock
    private ChatUserMapper mapper;

    @InjectMocks
    private ChatUserPersistenceAdapter adapter;

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        ChatUser chatUser = createValid();
        ChatUserEntity beforeSave = createValidBeforeSaved();

        when(mapper.toEntity(chatUser)).thenReturn(beforeSave);
        when(repo.save(beforeSave)).thenReturn(createValidSaved());

        adapter.save(chatUser);

        verify(mapper).toEntity(chatUser);
        verify(repo).save(beforeSave);

    }

    @Test
    void updateLastReadChat는_Repository_메서드를_호출한다() {
        adapter.updateLastReadChat(1L, 1L, 2L);
        verify(repo).updateLastRead(1L, 1L, 2L);
    }

    private ChatUser createValid() {
        return new ChatUser(null, 1L, 1L, null);
    }

    private ChatUserEntity createValidBeforeSaved() {
        return new ChatUserEntity(null, 1L, 1L, null);
    }

    private ChatUserEntity createValidSaved() {
        return new ChatUserEntity(1L, 1L, 1L, null);
    }
}
