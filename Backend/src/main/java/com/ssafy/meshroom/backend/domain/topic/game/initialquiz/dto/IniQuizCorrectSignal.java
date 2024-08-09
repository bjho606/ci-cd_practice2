package com.ssafy.meshroom.backend.domain.topic.game.initialquiz.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IniQuizCorrectSignal {
    private String ovToken;
    private String submittedWord;
    private boolean result;
}
