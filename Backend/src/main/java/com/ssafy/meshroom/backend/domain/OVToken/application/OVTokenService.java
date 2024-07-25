package com.ssafy.meshroom.backend.domain.OVToken.application;

import com.ssafy.meshroom.backend.domain.OVToken.dao.OVTokenRepository;
import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import com.ssafy.meshroom.backend.domain.user.application.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OVTokenService {
    private final OVTokenRepository ovTokenRepository;
    private final UserDetailService userDetailService;

    public void save(String sessionSid, String userSid) {
        ovTokenRepository.save(OVToken.builder()
                .sessionSid(sessionSid)
                .userSid(userSid)
                .build()
        );
    }

    public List<String> getUsersInSession(String sessionSid) {
        List<OVToken> li = ovTokenRepository.findAllBySessionSid(sessionSid);
        List<String> ret = new ArrayList<>();
        for (OVToken ovToken : li) {
            String userSid = ovToken.getUserSid();
            ret.add(userDetailService.loadUserByUsername(userSid).getUsername());
        }
        return ret;
    }

    public void removeSession(String sessionSid) {
        ovTokenRepository.deleteAllBySessionSid(sessionSid);
    }

    public void removeUserFromSession(String sessionSid, String userSid) {
        ovTokenRepository.deleteBySessionSidAndUserSid(sessionSid, userSid);
    }
}
