package com.ssafy.meshroom.backend.domain.OVToken.application;

import com.ssafy.meshroom.backend.domain.OVToken.dao.OVTokenRepository;
import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OVTokenService {
    private final OVTokenRepository ovTokenRepository;

    public void save(String sessionSid, String userSid){
        ovTokenRepository.save(OVToken.builder()
                        .SessionSid(sessionSid)
                        .userSid(userSid)
                        .build()
                                );
    }
}
