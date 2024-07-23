package com.ssafy.meshroom.backend.domain.session.application;

import com.ssafy.meshroom.backend.domain.OVToken.application.OVTokenService;
import com.ssafy.meshroom.backend.domain.contents.application.ContentsOrderService;
import com.ssafy.meshroom.backend.domain.session.dao.SessionRepository;
import com.ssafy.meshroom.backend.domain.session.dto.ConnectionCreateResponse;
import com.ssafy.meshroom.backend.domain.session.dto.SessionCreateResponse;
import com.ssafy.meshroom.backend.domain.session.dto.SubSessionCreateResponse;
import com.ssafy.meshroom.backend.domain.user.application.UserDetailService;
import com.ssafy.meshroom.backend.domain.user.domain.UserRole;
import com.ssafy.meshroom.backend.global.auth.jwt.TokenProvider;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
@Slf4j
public class SessionService {
    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;
    private final SessionRepository sessionRepository;
    private final ContentsOrderService contentsOrderService;
    private final UserDetailService userDetailService;
    private final TokenProvider tokenProvider;
    private final OVTokenService ovTokenService;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    @Transactional
    public Response<SessionCreateResponse> createSession(List<String> contents) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.createSession();

        com.ssafy.meshroom.backend.domain.session.domain.Session savedSession = sessionRepository.save(
                com.ssafy.meshroom.backend.domain.session.domain.Session.builder()
                        .sessionId(session.getSessionId())
                        .url("url")
                        .isMain(true)
                        .maxUserCount(60L)
                        .maxSubuserCount(10L)
                        .mainSession(null)
                        .build()
        );

        contentsOrderService.saveContentsOrder(savedSession.get_id(),contents);

        return new Response<SessionCreateResponse>(true,2010L, "SUCCESS",
                SessionCreateResponse.builder()
                        .sessionId(session.getSessionId())
                        .url("tmp_url")
                        .build()
                );
    }


    public Response<SubSessionCreateResponse> createSubSession(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.createSession();

        sessionRepository.save(
                com.ssafy.meshroom.backend.domain.session.domain.Session.builder()
                        .sessionId(session.getSessionId())
                        .url("url")
                        .isMain(false)
                        .maxUserCount(60L)
                        .maxSubuserCount(10L)
                        .mainSession(sessionId)
                        .build()
        );

        return new Response<SubSessionCreateResponse>(true,2010L, "SUCCESS",
                new SubSessionCreateResponse(session.getSessionId())
        );
    }

    @Transactional
    public Response<ConnectionCreateResponse> createConnection(String userName, String sessionId, HttpServletResponse response) throws OpenViduJavaClientException, OpenViduHttpException{
        // 1. 세션 인원 검사
        Session session = openvidu.getActiveSession(sessionId);
        session.fetch();
        int curCount = session.getActiveConnections().size();
        AtomicReference<UserRole> userRole = new AtomicReference<>(UserRole.PARTICIPANT);
        AtomicReference<com.ssafy.meshroom.backend.domain.session.domain.Session> sessionAtomicReference = new AtomicReference<>();
        sessionRepository.findBySessionId(sessionId).ifPresentOrElse(session1 -> {
            sessionAtomicReference.set(session1);
            if(session1.getIsMain()){
                if (curCount >= session1.getMaxUserCount()) {
                    throw new RuntimeException("꽉참");
                }
                if(curCount==0){
                    userRole.set(UserRole.FACILITATOR);
                }
            }else{
                if (curCount >= session1.getMaxSubuserCount()) {
                    throw new RuntimeException("꽉참");
                }
                if(curCount==0){
                    userRole.set(UserRole.TEAM_LEADER);
                }
            }
        }, () -> {
            throw new RuntimeException("없는 세션");
        });

        // 2. user 컬렉션과 token 컬렉션에 관계 추가
        String userId = userDetailService.saveUser(userName, userRole.get());
        ovTokenService.save(sessionAtomicReference.get().get_id(), userId);

        // 3-1. 유저 jwtToken 발행
        String jwtToken = tokenProvider.generateToken(userId, Duration.ofDays(10L));
        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setHttpOnly(true); // HTTP-Only 속성 설정
//        cookie.setSecure(true); // HTTPS로만 전송되도록 설정 (필요에 따라)
        cookie.setPath("/"); // 쿠키의 유효 경로 설정
        cookie.setMaxAge(60 * 60 * 24); // 쿠키의 유효 기간 설정 (초 단위, 여기서는 1일)
        response.addCookie(cookie);

        // 3-2. session 접속 토큰 발행
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .role(OpenViduRole.PUBLISHER)
                .data(userName)
                .build();
        Connection connection = session.createConnection(connectionProperties);
        String token = connection.getToken(); // Send this string to the client side
        return new Response<ConnectionCreateResponse>(true, 2010L, "SUCCESS"
                ,new ConnectionCreateResponse(token));
    }
}
