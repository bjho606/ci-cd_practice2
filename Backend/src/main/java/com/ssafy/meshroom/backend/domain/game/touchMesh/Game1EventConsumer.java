package com.ssafy.meshroom.backend.domain.game.touchMesh;

import com.ssafy.meshroom.backend.domain.game.GameEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class Game1EventConsumer {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "game-1-events")
    public void listen(GameEvent event) {
        // 이벤트 처리 로직
        CharacterUpdate update = processEvent(event);
        messagingTemplate.convertAndSend("/subscribe/game-1-events", update);

    }

    private CharacterUpdate processEvent(GameEvent event) {

        // 이벤트에 따라 캐릭터 상태를 업데이트하는 로직

        if ("INCREASE".equals(event.getType())) {
            return new CharacterUpdate(event.getSessionId(), "+");
        } else if ("DECREASE".equals(event.getType())) {
            return new CharacterUpdate(event.getSessionId(), "-");
        } else {
            return new CharacterUpdate(event.getSessionId(), "UNKNOWN");
        }

    }
}
