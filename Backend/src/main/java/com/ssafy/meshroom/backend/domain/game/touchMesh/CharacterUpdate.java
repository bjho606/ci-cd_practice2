package com.ssafy.meshroom.backend.domain.game.touchMesh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CharacterUpdate {
    // 버섯 키우기 캐릭터 상태 변화

    private String sessionId;
    private String status;
}
