package com.ll.playon.domain.chat.service;

import com.ll.playon.domain.chat.dto.ChatMemberDto;
import com.ll.playon.domain.chat.dto.ChatMessageDto;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.service.MemberTitleService;
import com.ll.playon.global.annotation.ChatMemberOnly;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final SimpMessageSendingOperations messagingTemplate;
    private final MemberTitleService memberTitleService;

    @ChatMemberOnly
    public void broadcastMessage(Member sender, long partyId, String message) {
        String title = this.memberTitleService.getRepresentativeTitle(sender);
        ChatMessageDto messageDto = ChatMessageDto.of(sender, title, message);

        this.messagingTemplate.convertAndSend("/topic/chat/party/" + partyId, messageDto);
    }

    public void broadcastMemberList(long partyId, List<ChatMemberDto> chatMembers) {
        this.messagingTemplate.convertAndSend("/topic/chat/party/" + partyId + "/members", chatMembers);
    }

    public void broadcastEnterMessage(long partyId, Member sender, String title) {
        ChatMessageDto messageDto = ChatMessageDto.enter(sender, title);

        this.messagingTemplate.convertAndSend("/topic/chat/party/" + partyId, messageDto);
    }

    public void broadcastLeaveMessage(long partyId, Member sender, String title) {
        ChatMessageDto messageDto = ChatMessageDto.leave(sender, title);

        this.messagingTemplate.convertAndSend("/topic/chat/party/" + partyId, messageDto);
    }
}
