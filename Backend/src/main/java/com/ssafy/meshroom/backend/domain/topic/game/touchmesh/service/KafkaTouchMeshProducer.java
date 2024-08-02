package com.ssafy.meshroom.backend.domain.topic.game.touchmesh.service;

import com.ssafy.meshroom.backend.domain.OVToken.dao.OVTokenRepository;
import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import com.ssafy.meshroom.backend.domain.session.dao.SessionRepository;
import com.ssafy.meshroom.backend.domain.session.domain.Session;
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto.TouchType.DECREASE;
import static com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto.TouchType.INCREASE;

@Service
@AllArgsConstructor
public class KafkaTouchMeshProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private OVTokenRepository ovTokenRepository;
    private SessionRepository sessionRepository;

    private static final String TOPIC = "game-touchmesh";

    public void sendEvent(String userSid, TouchDto touchEvent) {

        List<OVToken> list = ovTokenRepository.findAllByUserSid(userSid);
        Session mySession = null;

        for (int i = 0; i < list.size(); i++) {

            OVToken ovToken = list.get(i);
            String findSession = ovToken.getSessionSid();
            Optional<Session> testSession = sessionRepository.findById(findSession);

            if (!testSession.get().getIsMain()) {
                mySession = testSession.get();
            } else {
                continue;
            }
        }

        TouchDto filteredTouchEvent = new TouchDto();

        filteredTouchEvent.setMainSessionId(mySession.getMainSession());
        filteredTouchEvent.setSessionId(mySession.getSessionId());

        if (mySession.equals(touchEvent.getSessionId())) {
            filteredTouchEvent.setType(INCREASE);
        } else {
            filteredTouchEvent.setType(DECREASE);
        }


        // session Sid  두개 뽑을 수 있음
        // findby id Session객체
        // isMain, sessionId
        // => 내 그룹의 sessionID
        // 사용자가 클릭한 sessionID


        kafkaTemplate.send(TOPIC, filteredTouchEvent);


    }
}
