package com.ssafy.meshroom.backend.domain.OVToken.application;

import com.ssafy.meshroom.backend.domain.OVToken.dao.OVTokenRepository;
import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import com.ssafy.meshroom.backend.domain.session.dao.SessionRepository;
import com.ssafy.meshroom.backend.domain.session.domain.Session;
import com.ssafy.meshroom.backend.domain.user.application.UserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class OVTokenService {
    private final OVTokenRepository ovTokenRepository;
    private final UserDetailService userDetailService;
    private final SessionRepository sessionRepository;

    public void save(String sessionSid, String userSid, String ovToken) {
        ovTokenRepository.save(OVToken.builder()
                .sessionSid(sessionSid)
                .userSid(userSid)
                .ovToken(ovToken)
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

    public Long getUserCountInSession(String sessionSid) {
        List<OVToken> li = ovTokenRepository.findAllBySessionSid(sessionSid);
        return (long)li.size();
    }

    public void removeSession(String sessionSid) {
        ovTokenRepository.deleteAllBySessionSid(sessionSid);
    }

    public void removeUserFromSession(String sessionSid, String userSid) {
        ovTokenRepository.deleteBySessionSidAndUserSid(sessionSid, userSid);
    }

    public Session getMainSessionFromUserId(String userSid){
        List<OVToken> ovTokens = ovTokenRepository.findAllByUserSid(userSid);
        for(OVToken ovToken: ovTokens){
            Session s = sessionRepository.findById(ovToken.getSessionSid()).orElseThrow();
            if(s.getIsMain()){ return s; }
        }

        return null;
    }
    public Session deleteAllSubSessionConnectionFromUserId(String userSid){
        List<OVToken> ovTokens = ovTokenRepository.findAllByUserSid(userSid);
        Session ret = null;
        for(OVToken ovToken: ovTokens){
            Session s = sessionRepository.findById(ovToken.getSessionSid()).orElseThrow();
            if(s.getIsMain()) {
                ret = s;
                continue;
            }
            removeUserFromSession(s.get_id(), userSid);
        }

        return ret;
    }

    public boolean checkIfTokenExists(String userSid, String sessionSid) {
        return ovTokenRepository.existsByUserSidAndSessionSid(userSid, sessionSid);
    }
}
