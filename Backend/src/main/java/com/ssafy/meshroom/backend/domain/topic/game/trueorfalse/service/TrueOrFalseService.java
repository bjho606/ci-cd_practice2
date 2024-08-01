package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.service;

import com.ssafy.meshroom.backend.domain.session.dto.SessionCreateResponse;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dao.TrueOrFalseRepository;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain.TFInfo;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoCreateRequest;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoCreateResponse;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoRequest;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.TFInfoResponse;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrueOrFalseService {
    private final TrueOrFalseRepository trueOrFalseRepository;

    public Response<TFInfoCreateResponse> createTFInfo(String sessionId, TFInfoCreateRequest tfInfoCreateRequest) {
        trueOrFalseRepository.save(TFInfo.builder()
                        .ovToken(tfInfoCreateRequest.getOvToken())
                        .sessionId(sessionId)
                        .truth1(tfInfoCreateRequest.getTruth1())
                        .truth2(tfInfoCreateRequest.getTruth2())
                        .truth3(tfInfoCreateRequest.getTruth3())
                        .truth4(tfInfoCreateRequest.getTruth4())
                        .false1(tfInfoCreateRequest.getFalse1())
                .build());

        return new Response<TFInfoCreateResponse>(true, 2010L, "SUCCESS",
                TFInfoCreateResponse.builder()
                        .isCreated(true)
                        .build()
        );
    }

    public Response<TFInfoResponse> getTFInfo(String sessionId, TFInfoRequest tfInfoRequest) {
        TFInfo foundTFInfo = trueOrFalseRepository.findByOvTokenAndSessionId(tfInfoRequest.getOvToken(), sessionId)
                .orElseThrow(()-> new RuntimeException("진실 혹은 거짓 작성 내용이 없습니다."));

        return new Response<TFInfoResponse>(true, 2000L, "SUCCESS",
                TFInfoResponse.from(foundTFInfo)
        );
    }
}
