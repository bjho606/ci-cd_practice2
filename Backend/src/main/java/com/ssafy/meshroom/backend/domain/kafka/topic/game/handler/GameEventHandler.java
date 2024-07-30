package com.ssafy.meshroom.backend.domain.kafka.topic.game.handler;

import com.ssafy.meshroom.backend.domain.kafka.topic.game.touchmesh.dto.TouchDto;
import com.ssafy.meshroom.backend.domain.kafka.topic.game.touchmesh.service.KafkaTouchMeshProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameEventHandler {

    @Autowired
    private KafkaTouchMeshProducer kafkaTouchMeshProducer;

    @MessageMapping("/game/touch")
    public void handleGameEvent(TouchDto touchEvent) {
        kafkaTouchMeshProducer.sendEvent(touchEvent);
    }

    // TODO: 게임 더 추가
}
