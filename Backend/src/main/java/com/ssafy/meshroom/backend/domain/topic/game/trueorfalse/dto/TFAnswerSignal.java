package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TFAnswerSignal {
    private String ovToken;
    private int chosen;
}
