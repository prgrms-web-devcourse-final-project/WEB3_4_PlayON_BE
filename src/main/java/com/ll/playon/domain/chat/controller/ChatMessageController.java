package com.ll.playon.domain.chat.controller;

import com.ll.playon.domain.chat.service.ChatMessageService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.webSocket.WebSocketUserContext;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final WebSocketUserContext webSocketUserContext;
    private final ChatMessageService chatMessageService;

    // TODO: 배포 시 주석 해제
    @MessageMapping("/chat.send/{partyId}")
    public void handleMessage(@DestinationVariable long partyId, @Payload String message, Principal principal) {
        Member sender = this.webSocketUserContext.getActor(principal);

        this.chatMessageService.broadcastMessage(partyId, sender, message);
    }

    @MessageMapping("/chat.send/{partyId}/member/{memberId}")
    public void handleMessage(@DestinationVariable long partyId, @DestinationVariable long memberId, @Payload String message) {
        Member sender = this.webSocketUserContext.findById(memberId);

        this.chatMessageService.broadcastMessage(partyId, sender, message);
    }
}
