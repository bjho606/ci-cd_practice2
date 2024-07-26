package com.ssafy.meshroom.backend.domain.chat.api;

import com.ssafy.meshroom.backend.domain.chat.application.ChatService;
import com.ssafy.meshroom.backend.domain.chat.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/join")
    public void join(ChatMessage chatMessage) {
        log.info("MessageMapping /chat/join");
        chatMessage.setContent(chatMessage.getSessionSid() + " 님이 입장하셨습니다.");
        chatService.join(chatMessage);
    }

    // Client가 SEND할 수 있는 경로
    // WebSocketConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    // "/publish/chat/message"
    @MessageMapping("/chat/message")
//    @SendTo("/chat/message")
    public void sendMessage(ChatMessage chatMessage) {
        log.info("Message Mapping /chat/message");
        chatService.sendChatMessage(chatMessage);
    }

    @RequestMapping(value = "/chat/message", produces = "application/json", method = {RequestMethod.POST})
    public void messagePost(@RequestBody ChatMessage chatMessage) {
        log.info("Request Mapping /chat/message");
        chatService.sendChatMessage(chatMessage);
    }
}
