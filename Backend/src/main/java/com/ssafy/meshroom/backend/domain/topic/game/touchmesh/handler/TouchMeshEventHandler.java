package com.ssafy.meshroom.backend.domain.topic.game.touchmesh.handler;

import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto;
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.service.KafkaTouchMeshProducer;
import com.ssafy.meshroom.backend.domain.user.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TouchMeshEventHandler {

    @Autowired
    private KafkaTouchMeshProducer kafkaTouchMeshProducer;
    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/game/touch")
    public void handleTouchEvent(Principal p, TouchDto touchEvent) {
        String userSid = p.getName();
        kafkaTouchMeshProducer.sendEvent(userSid, touchEvent);

    }

}
