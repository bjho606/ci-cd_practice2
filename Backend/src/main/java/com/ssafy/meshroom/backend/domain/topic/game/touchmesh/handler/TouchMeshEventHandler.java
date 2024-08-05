package com.ssafy.meshroom.backend.domain.topic.game.touchmesh.handler;

import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto;
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.service.KafkaTouchMeshProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

<<<<<<< HEAD
import java.security.Principal;

=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
@Controller
public class TouchMeshEventHandler {

    @Autowired
    private KafkaTouchMeshProducer kafkaTouchMeshProducer;

    @MessageMapping("/game/touch")
    public void handleTouchEvent(TouchDto touchEvent) {
<<<<<<< HEAD
        System.out.println(touchEvent.toString());
        kafkaTouchMeshProducer.sendEvent(touchEvent);


=======
        kafkaTouchMeshProducer.sendEvent(touchEvent);
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
    }

}
