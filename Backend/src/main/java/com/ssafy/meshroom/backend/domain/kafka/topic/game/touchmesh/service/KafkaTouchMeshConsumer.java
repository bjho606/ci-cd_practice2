package com.ssafy.meshroom.backend.domain.kafka.topic.game.touchmesh.service;

import com.ssafy.meshroom.backend.domain.kafka.topic.game.touchmesh.dto.TouchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaTouchMeshConsumer {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "game-touchmesh")
    public void listen(TouchDto touchEvent) {

        messagingTemplate.convertAndSend("/subscribe/game-touchmesh", touchEvent);

    }

}
