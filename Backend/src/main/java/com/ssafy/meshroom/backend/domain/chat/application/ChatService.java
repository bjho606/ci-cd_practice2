package com.ssafy.meshroom.backend.domain.chat.application;

import com.ssafy.meshroom.backend.domain.chat.domain.ChatMessage;
import com.ssafy.meshroom.backend.domain.session.application.SessionService;
import com.ssafy.meshroom.backend.domain.user.application.UserDetailService;
import com.ssafy.meshroom.backend.domain.user.domain.User;
import com.ssafy.meshroom.backend.global.infra.kafka.KafkaProducer;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {

    private final User user;
    private final SimpMessagingTemplate template;
    private final SessionService sessionService;
    private final KafkaProducer kafkaProducer;
    private final UserDetailService userDetailService;

    public void sendChatMessage(ChatMessage chatMessage) {
        kafkaProducer.sendMessage(chatMessage);
        template.convertAndSend("/subscribe/chat/session/" + chatMessage.getSessionSid(), chatMessage);
    }

    public void join(ChatMessage chatMessage) {
        kafkaProducer.sendMessage(chatMessage);
        template.convertAndSend("/subscribe/chat/session/" + chatMessage.getSessionSid(), chatMessage);
    }

    public void leave(ChatMessage chatMessage) throws OpenViduJavaClientException, OpenViduHttpException {
        kafkaProducer.sendMessage(chatMessage);
        sessionService.removeUserFromSession(chatMessage.getSessionSid(), user.get_id());
    }

}
