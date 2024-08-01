package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto;

import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain.TFInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TFInfoResponse {
    String ovToken;
    String sessionId;
    String truth1;
    String truth2;
    String truth3;
    String truth4;
    String false1;

    public static TFInfoResponse from (TFInfo tfInfo) {
        return new TFInfoResponse(
                tfInfo.getOvToken(),
                tfInfo.getSessionId(),
                tfInfo.getTruth1(),
                tfInfo.getTruth2(),
                tfInfo.getTruth3(),
                tfInfo.getTruth4(),
                tfInfo.getFalse1()
        );
    }
}
