package com.ssafy.meshroom.backend.domain.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameEvent {
    // 버섯키우기용
    private String sessionId;
    private String type;
}
