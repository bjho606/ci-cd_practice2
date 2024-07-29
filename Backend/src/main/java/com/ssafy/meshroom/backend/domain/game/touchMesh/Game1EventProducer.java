package com.ssafy.meshroom.backend.domain.game.touchMesh;

import com.ssafy.meshroom.backend.domain.game.GameEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Game1EventProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "game-1-events";

    public void sendEvent(GameEvent event) {
        kafkaTemplate.send(TOPIC, event);

    }
}
