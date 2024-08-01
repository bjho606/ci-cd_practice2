package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TFInfoCreateRequest {
    String ovToken;

    String truth1;
    String truth2;
    String truth3;
    String truth4;
    String false1;
}
