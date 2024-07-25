package com.ssafy.meshroom.backend.domain.kafka.topic.chat;

import com.ssafy.meshroom.backend.domain.chat.domain.ChatMessage;
import com.ssafy.meshroom.backend.domain.chat.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaChatConsumer {

    @KafkaListener(topics = "chat-message", groupId = "mesh-chat")
    public void listen(ChatMessage chatMessage) {

        if (chatMessage.getType() == MessageType.ALL) {
            log.info("<<<<<<<< kafka 전체 채팅 : " +  chatMessage);
        } else if (chatMessage.getType() == MessageType.GROUP) {
            log.info("<<<<<<<< kafka 그룹 채팅 : " +  chatMessage);
        } else {
            throw new RuntimeException("Unknown MessageType: " + chatMessage.getType());
        }

    }
}
