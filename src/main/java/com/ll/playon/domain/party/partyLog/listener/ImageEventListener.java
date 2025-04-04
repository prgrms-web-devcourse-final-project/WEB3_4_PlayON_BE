package com.ll.playon.domain.party.partyLog.listener;

import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.party.partyLog.event.ImageDeleteEvent;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.exceptions.EventListenerException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageEventListener {
    private final ImageService imageService;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(exceptionExpression = "#root instanceof T(java.lang.Exception)",
            maxAttemptsExpression = "#{3}",
            backoff = @Backoff(delay = 2000)
    )
    public void handleImageDelete(ImageDeleteEvent event) {
        try {
            this.imageService.deleteImageById(ImageType.LOG, event.logId());
            throw new RuntimeException("이벤트 리스너 에러 테스트");
        } catch (Exception ex) {
            // TODO: 이벤트 실패 시 알람?
            // TODO: 실패한 S3 삭제(처리) 방법 고안

            throw new EventListenerException(ErrorCode.EVENT_LISTENER_ERROR);
        }
    }
}
