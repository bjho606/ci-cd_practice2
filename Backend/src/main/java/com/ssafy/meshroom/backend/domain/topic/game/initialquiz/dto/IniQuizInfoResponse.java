package com.ssafy.meshroom.backend.domain.topic.game.initialquiz.dto;

import com.ssafy.meshroom.backend.domain.topic.game.initialquiz.domain.InitialQuizInfo;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain.TFInfo;
import com.ssafy.meshroom.backend.global.util.HangulToChosung;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IniQuizInfoResponse {
    private String userName;
    private String ovToken;
    private String categoryName;
    private String word;

    public static IniQuizInfoResponse from (InitialQuizInfo initialQuizInfo) {
        IniQuizInfoResponse iniQuizInfoResponse = new IniQuizInfoResponse();
        iniQuizInfoResponse.userName = initialQuizInfo.getUserName();
        iniQuizInfoResponse.ovToken = initialQuizInfo.getOvToken();
        iniQuizInfoResponse.categoryName = initialQuizInfo.getCategoryName();
//        iniQuizInfoResponse.word = initialQuizInfo.getWord();
        iniQuizInfoResponse.word = convertQuizWord(initialQuizInfo.getWord());

        return iniQuizInfoResponse;
    }

    private static String convertQuizWord(String quizWord) {

        return HangulToChosung.convertToChosung(quizWord);
    }
}
