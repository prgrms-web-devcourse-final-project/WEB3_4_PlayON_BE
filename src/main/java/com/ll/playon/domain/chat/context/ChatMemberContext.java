package com.ll.playon.domain.chat.context;

import com.ll.playon.domain.chat.entity.ChatMember;
import org.springframework.stereotype.Component;

@Component
public class ChatMemberContext {
    private static final ThreadLocal<ChatMember> currentChatMember = new ThreadLocal<>();

    public static ChatMember getChatMember() {
        return currentChatMember.get();
    }

    public static void setChatMember(ChatMember chatMember) {
        currentChatMember.set(chatMember);
    }

    public static void clear() {
        currentChatMember.remove();
    }
}
