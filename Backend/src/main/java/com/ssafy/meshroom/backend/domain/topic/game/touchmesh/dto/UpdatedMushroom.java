package com.ssafy.meshroom.backend.domain.topic.game.touchmesh.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedMushroom {
    private String mainSessionId;
    private String sessionId;
    private int size;
}
