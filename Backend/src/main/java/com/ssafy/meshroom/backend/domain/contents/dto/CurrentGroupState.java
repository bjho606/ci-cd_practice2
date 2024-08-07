package com.ssafy.meshroom.backend.domain.contents.dto;

import lombok.*;

import java.util.HashMap;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentGroupState {
    private HashMap<String, Boolean> getGroupStates;
    // 세션 Id, 상태
}
