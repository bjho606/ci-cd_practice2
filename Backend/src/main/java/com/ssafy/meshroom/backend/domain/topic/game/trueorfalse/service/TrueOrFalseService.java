package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.service;

import com.ssafy.meshroom.backend.domain.OVToken.dao.OVTokenRepository;
import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import com.ssafy.meshroom.backend.domain.session.dto.SessionCreateResponse;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dao.TrueOrFalseRepository;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain.TFInfo;
import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto.*;
import com.ssafy.meshroom.backend.domain.user.dao.UserRepository;
import com.ssafy.meshroom.backend.domain.user.domain.User;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrueOrFalseService {
    private final TrueOrFalseRepository trueOrFalseRepository;
    private final UserRepository userRepository;

    public Response<TFInfoCreateResponse> createTFInfo(String sessionId, String userSid, TFInfoCreateRequest tfInfoCreateRequest) {
        String userName = userRepository.findById(userSid)
                .orElseThrow(() -> new RuntimeException("유저 없음"))
                .getUsername();

        TFInfo newTFInfo = TFInfo.builder()
                            .userName(userName)
                            .ovToken(tfInfoCreateRequest.getOvToken())
                            .sessionId(sessionId)
                            .truths(tfInfoCreateRequest.getTruths())
                            .false1(tfInfoCreateRequest.getFalse1())
                        .build();

        if (trueOrFalseRepository.existsByOvTokenAndSessionId(tfInfoCreateRequest.getOvToken(), sessionId)) {
            TFInfo foundTFInfo = trueOrFalseRepository.findByOvTokenAndSessionId(tfInfoCreateRequest.getOvToken(), sessionId)
                            .orElseThrow(() -> new RuntimeException("정보가 존재하지 않음"));
            foundTFInfo.setTruths(tfInfoCreateRequest.getTruths());
            foundTFInfo.setFalse1(tfInfoCreateRequest.getFalse1());
            trueOrFalseRepository.save(foundTFInfo);
        } else {
            trueOrFalseRepository.save(newTFInfo);
        }

        return new Response<TFInfoCreateResponse>(true, 2010L, "SUCCESS",
                TFInfoCreateResponse.builder()
                        .isCreated(true)
                        .build()
        );
    }

    public Response<AllTFInfosResponse> getAllTFInfo(String sessionId) {
        List<TFInfo> tfInfoList = trueOrFalseRepository.findAllBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("해당 세션ID이 없습니다."));
        System.out.println(tfInfoList.stream().toString());

        List<TFInfoResponse> allTFInfos = tfInfoList.stream()
                .map(TFInfoResponse::from)
                .toList();

        return new Response<AllTFInfosResponse>(true, 2000L, "SUCCESS",
                new AllTFInfosResponse(allTFInfos)
        );
    }
}
