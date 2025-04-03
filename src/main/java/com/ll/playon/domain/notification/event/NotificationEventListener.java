package com.ll.playon.domain.notification.event;

import com.ll.playon.domain.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationSseService notificationSseService;

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("알림 이벤트 수신: receiverId={}, content={}",
                event.getReceiverId(), event.getResponse().content());

        notificationSseService.sendSseNotification(event.getReceiverId(), event.getResponse());
    }
}
