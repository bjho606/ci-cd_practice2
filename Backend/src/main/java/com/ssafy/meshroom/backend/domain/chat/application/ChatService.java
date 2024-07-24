package com.ssafy.meshroom.backend.domain.chat.application;

import com.ssafy.meshroom.backend.domain.chat.domain.ChatMessage;
import com.ssafy.meshroom.backend.domain.session.application.SessionService;
import com.ssafy.meshroom.backend.global.infra.kafka.KafkaProducer;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate template;
    private final SessionService sessionService;
    private final KafkaProducer kafkaProducer;

    public void sendChatMessage(ChatMessage chatMessage) {
        kafkaProducer.sendMessage(chatMessage);
        template.convertAndSend("/subscribe/chat/session/" + chatMessage.getSessionSid(), chatMessage);
    }

}
