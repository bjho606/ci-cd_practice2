package com.ssafy.meshroom.backend.global.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class HttpSessionHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("==============before handshake===============");
        if (request instanceof ServletServerHttpRequest servletServerRequest) {
//            HttpServletRequest servletRequest = servletServerRequest.getServletRequest();
//            Cookie token = CookieUtil.getCookie(servletRequest, "token").orElseThrow(AuthException::new);

            log.info("token:::");
//            log.info(token.getValue());
//            attributes.put("token", token.getValue());
            attributes.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtZXNocm9vbSIsImlhdCI6MTcyMjU3NjM4OCwiZXhwIjoxNzIzNDQwMzg4LCJzdWIiOiI2NmFiNGFiNzlhYWRlYzEzMGEyMTg4NmYifQ.zSu30cahYTH-1aSvvvimBBpp3UoDj7g-V2SxLyRBNY4");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("==============after handshake===============");
    }
}
