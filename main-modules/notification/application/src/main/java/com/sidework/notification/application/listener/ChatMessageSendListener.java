package com.sidework.notification.application.listener;

import com.sidework.common.event.ChatMessageSendEvent;
import com.sidework.notification.application.port.out.NotificationOutPort;
import com.sidework.notification.domain.Notification;
import com.sidework.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageSendListener {
    private final NotificationOutPort repo;

    @EventListener
    @Transactional
    public void onChatMessageSend(ChatMessageSendEvent event) {
        Notification notification = Notification.create(event.receiverId(), NotificationType.MESSAGE_ARRIVED,
                NotificationType.MESSAGE_ARRIVED.getValue(), event.message());

        repo.save(notification);
    }
}
