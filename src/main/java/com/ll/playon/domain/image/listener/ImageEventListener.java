package com.ll.playon.domain.image.listener;

import com.ll.playon.domain.image.event.ImageDeleteEvent;
import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.exceptions.EventListenerException;
import com.ll.playon.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageEventListener {
    private final ImageService imageService;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(exceptionExpression = "#root instanceof T(java.lang.Exception)",
            maxAttemptsExpression = "#{3}",
            backoff = @Backoff(delay = 2000)
    )
    public void handle(ImageDeleteEvent event) {
        try {
            this.imageService.deleteImageById(event.imageType(), event.id());
        } catch (ServiceException ex) {
            // TODO: 이벤트 실패 시 알람?
            // TODO: 실패한 S3 삭제(처리) 방법 고안

            throw new EventListenerException(ex.getResultCode(), ex.getMsg());
        } catch (Exception e) {
            throw new EventListenerException(ErrorCode.EVENT_LISTENER_ERROR);
        }
    }
}
