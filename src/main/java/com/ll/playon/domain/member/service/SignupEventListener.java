package com.ll.playon.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SignupEventListener {
    private final SteamAsyncService steamAsyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SignupEvent event) {
        steamAsyncService.getUserGamesAndCheckGenres(event.member());
    }
}