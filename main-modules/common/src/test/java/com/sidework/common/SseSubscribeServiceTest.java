package com.sidework.common;

import com.sidework.common.event.sse.port.out.SseSubscribeOutPort;
import com.sidework.common.event.sse.service.SseSubscribeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SseSubscribeServiceTest {
    @Mock
    private SseSubscribeOutPort sseSubscribeOutPort;


    @InjectMocks
    private SseSubscribeService service;

    @Test
    void subscribeUser는_성공시_SseEmitter를_반환한다() {
        SseEmitter sseEmitter = new SseEmitter();

        when(sseSubscribeOutPort.subscribeUser(1L)).thenReturn(sseEmitter);

        SseEmitter result = service.subscribeUser(1L);

        assertSame(sseEmitter, result);

        verify(sseSubscribeOutPort).subscribeUser(1L);
    }

    @Test
    void subscribeChat은_성공시_SseEmitter를_반환한다() {
        SseEmitter sseEmitter = new SseEmitter();

        when(sseSubscribeOutPort.subscribeChatRoom(1L)).thenReturn(sseEmitter);

        SseEmitter result = service.subscribeChat(1L);

        assertSame(sseEmitter, result);

        verify(sseSubscribeOutPort).subscribeChatRoom(1L);
    }
}
