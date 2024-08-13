package com.ssafy.meshroom.backend.domain.topic.game.initialquiz.dto;

import lombok.*;

import java.util.ArrayList;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IniQuizInfoCreateRequest {
    private String ovToken;
    private String categoryName;
    private String quizWord;
}
