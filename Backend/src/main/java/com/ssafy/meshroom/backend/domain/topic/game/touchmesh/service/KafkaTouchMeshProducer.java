package com.ssafy.meshroom.backend.domain.topic.game.touchmesh.service;

<<<<<<< HEAD
import com.ssafy.meshroom.backend.domain.OVToken.dao.OVTokenRepository;
import com.ssafy.meshroom.backend.domain.session.dao.SessionRepository;
=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaTouchMeshProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;
<<<<<<< HEAD
    private OVTokenRepository ovTokenRepository;
    private SessionRepository sessionRepository;
=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173

    private static final String TOPIC = "game-touchmesh";

    public void sendEvent(TouchDto touchEvent) {
<<<<<<< HEAD

        // session Sid  두개 뽑을 수 있음
        // findby id Session객체
        // isMain, sessionId
        // => 내 그룹의 sessionID
        // 사용자가 클릭한 sessionID
=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
        kafkaTemplate.send(TOPIC, touchEvent);
    }
}
