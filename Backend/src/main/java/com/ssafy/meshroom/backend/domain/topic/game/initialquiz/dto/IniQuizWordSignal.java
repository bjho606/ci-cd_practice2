package com.ssafy.meshroom.backend.domain.topic.game.initialquiz.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IniQuizWordSignal {
    private String ovToken;
    private String quizWord;
}
