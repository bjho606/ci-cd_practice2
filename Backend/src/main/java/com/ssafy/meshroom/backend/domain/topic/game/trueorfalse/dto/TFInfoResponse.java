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
    String sessionId;
    ArrayList<String> truths;
    String false1;

    public static TFInfoResponse from (TFInfo tfInfo) {
        return new TFInfoResponse(
                tfInfo.getOvToken(),
                tfInfo.getSessionId(),
                tfInfo.getTruths(),
                tfInfo.getFalse1()
        );
    }
}
