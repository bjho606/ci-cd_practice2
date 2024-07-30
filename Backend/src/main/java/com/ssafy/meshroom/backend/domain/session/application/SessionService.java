package com.ssafy.meshroom.backend.domain.session.application;

import com.ssafy.meshroom.backend.domain.OVToken.application.OVTokenService;
import com.ssafy.meshroom.backend.domain.contents.application.ContentsOrderService;
import com.ssafy.meshroom.backend.domain.session.dao.SessionRepository;
import com.ssafy.meshroom.backend.domain.session.dto.*;
import com.ssafy.meshroom.backend.domain.user.application.UserDetailService;
import com.ssafy.meshroom.backend.domain.user.domain.UserRole;
import com.ssafy.meshroom.backend.global.auth.jwt.TokenProvider;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import com.ssafy.meshroom.backend.global.util.CookieUtil;
import io.openvidu.java.client.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ContentsOrderService contentsOrderService;
    private final UserDetailService userDetailService;
    private final OpenViduService openViduService;
    private final TokenProvider tokenProvider;
    private final OVTokenService ovTokenService;


    @Transactional
    public Response<SessionCreateResponse> createSession(List<String> contents) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openViduService.createSession();

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

        contentsOrderService.saveContentsOrder(savedSession.get_id(), contents);

        return new Response<SessionCreateResponse>(true, 2010L, "SUCCESS",
                SessionCreateResponse.builder()
                        .sessionId(session.getSessionId())
                        .url("tmp_url")
                        .build()
        );
    }

    @Transactional
    public Response<SubSessionCreateResponse> createSubSession(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openViduService.createSession();

        sessionRepository.save(
                com.ssafy.meshroom.backend.domain.session.domain.Session.builder()
                        .sessionId(session.getSessionId())
                        .url("url")
                        .isMain(false)
                        .groupName("그룹")
                        .maxUserCount(60L)
                        .maxSubuserCount(10L)
                        .mainSession(sessionId)
                        .build()
        );

        return new Response<SubSessionCreateResponse>(true, 2010L, "SUCCESS",
                new SubSessionCreateResponse(session.getSessionId())
        );
    }

    @Transactional
    public Response<ConnectionCreateResponse> createConnection(String userName, String sessionId, HttpServletResponse response) throws OpenViduJavaClientException, OpenViduHttpException {
        // 1. 세션 인원 검사
        Session session = openViduService.getSession(sessionId);
        long curCount = openViduService.getSessionCount(session);

        AtomicReference<UserRole> userRole = new AtomicReference<>(UserRole.PARTICIPANT);
        AtomicReference<com.ssafy.meshroom.backend.domain.session.domain.Session> sessionAtomicReference = new AtomicReference<>();
        sessionRepository.findBySessionId(sessionId).ifPresentOrElse(session1 -> {
            sessionAtomicReference.set(session1);
            if (session1.getIsMain()) {
                if (curCount >= session1.getMaxUserCount()) {
                    throw new RuntimeException("꽉참");
                }
                if (curCount == 0) {
                    userRole.set(UserRole.FACILITATOR);
                }
            } else {
                if (curCount >= session1.getMaxSubuserCount()) {
                    throw new RuntimeException("꽉참");
                }
                if (curCount == 0) {
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
        CookieUtil.addCookie(response, "token", jwtToken);

        // 3-2. session 접속 토큰 발행
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .role(OpenViduRole.PUBLISHER)
                .data(userName)
                .build();
        Connection connection = session.createConnection(connectionProperties);
        String token = connection.getToken(); // Send this string to the client side
        return new Response<ConnectionCreateResponse>(true, 2010L, "SUCCESS"
                , new ConnectionCreateResponse(token));
    }

    @Transactional
    public Response<SessionInfoResponse> getSessionInfo(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        AtomicReference<SessionInfoResponse> sessionInfo = new AtomicReference<>();
        List<SubSessionInfoResponse> subs = new ArrayList<>();
        sessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            sessionRepository.findAllByMainSession(sessionId).ifPresent(subSessions -> {
                subSessions.forEach((subSession) -> {
                    try {
                        subs.add(getSubsession(sessionId, subSession.getSessionId()).orElseThrow());
                    } catch (OpenViduJavaClientException | OpenViduHttpException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            try {
                sessionInfo.set(SessionInfoResponse.builder()
                        .maxUserCount(session.getMaxUserCount())
                        .currentUserCount(openViduService.getSessionCount(openViduService.getSession(sessionId)))
                        .url(session.getUrl())
                        .groups(subs)
                        .build()
                );
            } catch (OpenViduJavaClientException | OpenViduHttpException e) {
                throw new RuntimeException(e);
            }
        });

        return new Response<SessionInfoResponse>(true, 2010L, "SUCCESS",
                sessionInfo.get());
    }

    public Response<SubSessionInfoResponse> getSubSessionInfo(String sessionId, String subSessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        return new Response<SubSessionInfoResponse>(true, 2000L, "SUCCESS"
                , getSubsession(sessionId, subSessionId).orElseThrow());
    }

    public Optional<SubSessionInfoResponse> getSubsession(String sessionId, String subSessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        AtomicReference<SubSessionInfoResponse> ret = new AtomicReference<>();

        sessionRepository.findBySessionId(subSessionId).ifPresentOrElse(
                (session) -> {
                    // 1. subSession과 session이 부모자식관계인지
                    if (!session.getMainSession().equals(sessionId)) {
                        throw new RuntimeException();
                    }

                    ret.set(SubSessionInfoResponse.builder()
                            .sessionId(subSessionId)
                            .maxUserCount(sessionRepository.findBySessionId(sessionId).orElseThrow().getMaxSubuserCount())
                            .groupName(session.getGroupName())
                            .username(
                                    ovTokenService.getUsersInSession(session.get_id())
                            )
                            .build()
                    );
                }
                , () -> {
                    throw new RuntimeException();
                }
        );
        Session session = openViduService.getSession(sessionId);
        ret.get().setCurrentUserCount(openViduService.getSessionCount(session));
//        ret.get().setUsername(openViduService.getUsernameInSession(session))
        return Optional.ofNullable(ret.get());
    }

    @Transactional
    public Response<?> deleteSession(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        com.ssafy.meshroom.backend.domain.session.domain.Session _session
                = sessionRepository.findBySessionId(sessionId).orElseThrow();

        if (_session.getIsMain()) {
            ovTokenService.removeSession(_session.get_id());
            sessionRepository.deleteAllByMainSession(sessionId).orElseThrow()
                    .forEach((session -> {
                        try {
                            openViduService.getSession(session.getSessionId()).close();
                        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
                            throw new RuntimeException(e);
                        }
                    }));
            sessionRepository.deleteById(_session.get_id());
        }

        Session session = openViduService.getSession(sessionId);
        session.close();

        return new Response<>(true, 2000L, "삭제 성공", null);
    }

    @Transactional
    public Response<?> updateSessionUserCounts(String sessionId, UpdateSessionRequest request) {
        com.ssafy.meshroom.backend.domain.session.domain.Session session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다"));

        session.setMaxUserCount(request.getMaxUserCount());
        session.setMaxSubuserCount(request.getMaxSubuserCount());
        sessionRepository.save(session);

        return new Response<>(true, 2000L, "세션 정보가 성공적으로 수정되었습니다.", null);
    }

    @Transactional
    public Response<?> updateSubSessionGroupName(String subsessionId, UpdateGroupNameRequest request) {
        com.ssafy.meshroom.backend.domain.session.domain.Session session = sessionRepository.findBySessionId(subsessionId)
                .orElseThrow(() -> new RuntimeException("하위 세션을 찾을 수 없습니다"));

        session.setGroupName(request.getGroupName());
        sessionRepository.save(session);

        return new Response<>(true, 2000L, "하위 세션의 그룹 이름이 성공적으로 수정되었습니다.", null);
    }

    @Transactional
    public Response<?> removeUserFromSession(String sessionSid, String userSid) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openViduService.getSession(sessionSid);

        // OpenVidu에서 사용자를 세션에서 제거
        Connection connection = session.getConnections().stream()
                .filter(conn -> conn.getConnectionId().equals(userSid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in session"));
        session.forceDisconnect(connection);

        // 데이터베이스에서 사용자 제거
        ovTokenService.removeUserFromSession(sessionSid, userSid);

        return new Response<>(true, 2000L, "사용자가 세션에서 성공적으로 제거되었습니다.", null);
    }


}
