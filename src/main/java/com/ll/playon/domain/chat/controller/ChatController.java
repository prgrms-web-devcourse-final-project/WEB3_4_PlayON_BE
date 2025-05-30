package com.ll.playon.domain.chat.controller;

import com.ll.playon.domain.chat.dto.GetChatRoomResponse;
import com.ll.playon.domain.chat.service.ChatService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserContext userContext;

    @PostMapping("/enter/{partyId}")
    @Operation(summary = "채팅방 입장")
    public RsData<GetChatRoomResponse> enterPartyRoom(@PathVariable long partyId) {
        Member actor = this.userContext.getActualActor();

        return RsData.success(HttpStatus.OK, this.chatService.enterPartyRoom(actor, partyId));
    }

    @DeleteMapping("/leave/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "채팅방 퇴장")
    public void leavePartyRoom(@PathVariable long partyId) {
        Member actor = this.userContext.getActualActor();

        this.chatService.leavePartyRoom(actor, partyId);
    }
}
