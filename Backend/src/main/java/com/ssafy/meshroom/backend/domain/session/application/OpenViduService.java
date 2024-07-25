package com.ssafy.meshroom.backend.domain.session.application;

import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenViduService {
    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    public Session createSession() throws OpenViduJavaClientException, OpenViduHttpException {
        return openvidu.createSession();
    }

    public Session getSession(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        return openvidu.getActiveSession(sessionId);
    }

    public long getSessionCount(Session session) throws OpenViduJavaClientException, OpenViduHttpException {
        session.fetch();
        return session.getActiveConnections().size();
    }

    public List<String> getUsernameInSession(Session session) throws OpenViduJavaClientException, OpenViduHttpException {
        session.fetch();
        return session.getActiveConnections().stream()
                .map(Connection::getServerData)
                .collect(Collectors.toList());
    }

    public void forceDisconnet(Session session, Connection connection) throws OpenViduJavaClientException, OpenViduHttpException {
        session.forceDisconnect(connection);
    }
}
