package com.ssafy.meshroom.backend.global.infra.kafka;

import com.ssafy.meshroom.backend.domain.chat.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaProducer {
    private static final String TOPIC_CHAT_MESSAGE = "chat_message";

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public void sendMessage(ChatMessage chatMessage) {

        log.info(">>>>>>>> kafka send message : " +  chatMessage);
        kafkaTemplate.send(TOPIC_CHAT_MESSAGE, chatMessage);
    }
}
