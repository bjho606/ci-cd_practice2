package com.ssafy.meshroom.backend.global.config.websocket;

import com.ssafy.meshroom.backend.global.auth.jwt.TokenProvider;
<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
=======
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketInterceptor implements ChannelInterceptor {
    private final TokenProvider jwtProvider;
<<<<<<< HEAD
=======

>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
<<<<<<< HEAD
            log.info("session attributes token : " + (String) sessionAttributes.get("token"));
=======
            log.info((String) sessionAttributes.get("token"));
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
            Authentication authentication = jwtProvider.getAuthentication((String) sessionAttributes.get("token"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);
        }
<<<<<<< HEAD

=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
        return message;
    }

}
