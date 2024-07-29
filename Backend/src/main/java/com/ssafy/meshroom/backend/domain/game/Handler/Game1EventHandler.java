package com.ssafy.meshroom.backend.domain.game.Handler;

import com.ssafy.meshroom.backend.domain.game.GameEvent;
import com.ssafy.meshroom.backend.domain.game.touchMesh.Game1EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class Game1EventHandler {

    @Autowired
    private Game1EventProducer game1EventProducer;

    @MessageMapping("/game-1/event")
    public void handleGameEvent(GameEvent event) {
        game1EventProducer.sendEvent(event);
    }

}
