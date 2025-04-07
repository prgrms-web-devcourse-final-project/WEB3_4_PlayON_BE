package com.ll.playon.domain.notification.event;

import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private final Long receiverId;
    private final NotificationResponse response;

    public NotificationEvent(Object source, Long receiverId, NotificationResponse response) {
        super(source);
        this.receiverId = receiverId;
        this.response = response;
    }
}
