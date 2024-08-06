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
    String userName;
    String ovToken;
    ArrayList<String> statements;
    int falseIndex;

    public static TFInfoResponse from (TFInfo tfInfo) {
        TFInfoResponse tfInfoResponse = new TFInfoResponse();
        tfInfoResponse.userName = tfInfo.getUserName();
        tfInfoResponse.ovToken = tfInfo.getOvToken();

        ArrayList<String> mixedStatements = new ArrayList<>(tfInfo.getTruths());
        tfInfoResponse.falseIndex = (int)(Math.random()*10) % tfInfo.getTruths().size();
        mixedStatements.add(tfInfoResponse.falseIndex, tfInfo.getFalse1());

        tfInfoResponse.statements = mixedStatements;

        return tfInfoResponse;
    }
}
