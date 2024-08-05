package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto;

import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain.TFInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TFInfoResponse {
    String ovToken;
<<<<<<< HEAD
=======
    String sessionId;
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
    ArrayList<String> truths;
    String false1;

    public static TFInfoResponse from (TFInfo tfInfo) {
        return new TFInfoResponse(
                tfInfo.getOvToken(),
<<<<<<< HEAD
=======
                tfInfo.getSessionId(),
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
                tfInfo.getTruths(),
                tfInfo.getFalse1()
        );
    }
}
