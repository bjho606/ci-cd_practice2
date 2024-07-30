package com.ssafy.meshroom.backend.domain.kafka.topic.chat.handler;

import com.ssafy.meshroom.backend.domain.kafka.topic.chat.service.ChatService;
import com.ssafy.meshroom.backend.domain.kafka.topic.chat.dto.ChatMessagePublish;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class ChatEventHandler {

    private final ChatService chatService;

    @MessageMapping("/chat/join")
    public void join(ChatMessagePublish chatMessagePublish) {
        log.info("MessageMapping /chat/join");

        chatService.join(chatMessagePublish);
    }

    // Client가 SEND할 수 있는 경로
    // WebSocketConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    // "/publish/chat/message"
    @MessageMapping("/chat/message")
//    @SendTo("/chat/message")
    public void sendMessage(ChatMessagePublish chatMessagePublish) {
        log.info("Message Mapping /chat/message");

        chatService.sendChatMessage(chatMessagePublish);
    }
}
