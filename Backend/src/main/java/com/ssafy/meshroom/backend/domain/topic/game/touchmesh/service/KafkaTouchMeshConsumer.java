package com.ssafy.meshroom.backend.domain.topic.game.touchmesh.service;

<<<<<<< HEAD
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto;
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.UpdatedMushroom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
=======
import com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto.TouchDto;
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
@Slf4j
@Service
@AllArgsConstructor
public class KafkaTouchMeshConsumer {

    @Autowired
    private final RedisTemplate<String, Integer> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "game-touchmesh", groupId = "mesh-chat")
    public void listen(TouchDto touchEvent) throws JsonProcessingException {
        updateMushroomSize(touchEvent);
        sendUpdateToClient(touchEvent);
    }

    private void updateMushroomSize(TouchDto touchEvent) {
        String key = touchEvent.getSessionId();

        // Redis에셔 현재 크기 가져오기
        Integer currSize = redisTemplate.opsForValue().get(key);

        System.out.println(touchEvent.getType());


        if (currSize == null) {
            currSize = 100;
            redisTemplate.opsForValue().set(key, currSize);
        } else {
            if (touchEvent.getType() == TouchDto.TouchType.DECREASE) {
                redisTemplate.opsForValue().set(key, currSize - 1);
            } else {
                redisTemplate.opsForValue().set(key, currSize + 1);
            }
        }
    }

    private void sendUpdateToClient(TouchDto touchEvent) throws JsonProcessingException {

        Integer updatedSize = redisTemplate.opsForValue().get(touchEvent.getSessionId());

        if (updatedSize == null) {
            updatedSize = 100;
        }


        UpdatedMushroom updatedMushroom = new UpdatedMushroom();
        updatedMushroom.setMainSessionId(touchEvent.getMainSessionId());
        updatedMushroom.setSessionId(touchEvent.getSessionId());
        updatedMushroom.setSize(updatedSize);

        String message = objectMapper.writeValueAsString(updatedMushroom);
        log.info(message);
        messagingTemplate.convertAndSend("/subscribe/game/touch/" + touchEvent.getMainSessionId(), message);

=======
@Service
public class KafkaTouchMeshConsumer {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "game-touchmesh")
    public void listen(TouchDto touchEvent) {

        messagingTemplate.convertAndSend("/subscribe/game-touchmesh", touchEvent);
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173

    }

}
