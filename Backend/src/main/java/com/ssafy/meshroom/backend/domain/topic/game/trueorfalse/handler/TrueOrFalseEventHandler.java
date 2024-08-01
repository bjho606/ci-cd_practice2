package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.handler;

import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoCreateRequest;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoCreateResponse;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoRequest;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoResponse;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.service.TrueOrFalseService;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "게임-진실혹은거짓 API", description = "진실 혹은 거짓 게임을 위한 API")
public class TrueOrFalseEventHandler {
    private final TrueOrFalseService trueOrFalseService;
//    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/api/v1/game/tf/{sessionId}")
    public ResponseEntity<Response<TFInfoCreateResponse>> createTFInfo (
            @PathVariable String sessionId,
            @RequestBody TFInfoCreateRequest tfInfoCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trueOrFalseService.createTFInfo(sessionId, tfInfoCreateRequest));
    }

    @GetMapping("/api/v1/game/tf/{sessionId}")
    public ResponseEntity<Response<TFInfoResponse>> getTFInfo (
            @PathVariable String sessionId,
            @RequestBody TFInfoRequest tfInfoRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(trueOrFalseService.getTFInfo(sessionId, tfInfoRequest));
    }

    @MessageMapping("/game/tf/question")
    @SendTo("/subscribe/game/tf/question")
    public Boolean handleSubmitTF(Boolean isDone) {
        log.info("submit signal recieved : " + isDone);
//        messagingTemplate.convertAndSend("/subscribe/game/tf/question", isDone);
        return isDone;
    }

    @MessageMapping("/game/tf/answer")
    @SendTo("/subscribe/game/tf/answer")
    public int handleAnswerTF(int chosen) {
        log.info("choose number signal recieved : " + chosen);
        return chosen;
    }

    @MessageMapping("/game/tf/finish")
    @SendTo("/subscribe/game/tf/finish")
    public String handleFinishTF(String sessionId) {
        log.info("finished signal recieved : " + sessionId);
        return sessionId;
    }
}
