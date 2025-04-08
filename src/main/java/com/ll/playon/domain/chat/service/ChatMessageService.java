package com.ll.playon.domain.chat.service;

import com.ll.playon.domain.chat.dto.ChatMemberDto;
import com.ll.playon.domain.chat.dto.ChatMessageDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final SimpMessageSendingOperations messagingTemplate;

    public void broadcastMessage(long partyId, ChatMessageDto chatMessageDto) {
        this.messagingTemplate.convertAndSend("/topic/chat/party/" + partyId, chatMessageDto);
    }

    public void broadcastMemberList(long partyId, List<ChatMemberDto> chatMembers) {
        this.messagingTemplate.convertAndSend("/topic/chat/party/" + partyId + "/members", chatMembers);
    }
}
