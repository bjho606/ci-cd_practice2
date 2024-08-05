package com.ssafy.meshroom.backend.domain.topic.game.touchmesh.service;

import com.ssafy.meshroom.backend.domain.OVToken.dao.OVTokenRepository;
import com.ssafy.meshroom.backend.domain.session.dao.SessionRepository;
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaTouchMeshProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private OVTokenRepository ovTokenRepository;
    private SessionRepository sessionRepository;

    private static final String TOPIC = "game-touchmesh";

    public void sendEvent(TouchDto touchEvent) {

        // session Sid  두개 뽑을 수 있음
        // findby id Session객체
        // isMain, sessionId
        // => 내 그룹의 sessionID
        // 사용자가 클릭한 sessionID
        kafkaTemplate.send(TOPIC, touchEvent);
    }
}
