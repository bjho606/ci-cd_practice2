package com.ssafy.meshroom.backend.domain.kafka.topic.game.touchmesh.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TouchDto {
    //  세션id와 +/- 정보
    private String sessionId;
    private enum type {INCREASE, DECREASE};
}