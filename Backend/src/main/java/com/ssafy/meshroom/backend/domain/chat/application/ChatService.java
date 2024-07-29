package com.ssafy.meshroom.backend.domain.chat.application;

import com.ssafy.meshroom.backend.domain.chat.domain.ChatMessage;
import com.ssafy.meshroom.backend.domain.chat.dto.ChatMessagePublish;
import com.ssafy.meshroom.backend.domain.chat.dto.ChatMessageSubscribe;
import com.ssafy.meshroom.backend.domain.session.application.SessionService;
import com.ssafy.meshroom.backend.domain.user.application.UserDetailService;
import com.ssafy.meshroom.backend.domain.user.dao.UserRepository;
import com.ssafy.meshroom.backend.domain.user.domain.User;
import com.ssafy.meshroom.backend.domain.kafka.topic.chat.KafkaChatProducer;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {

    private final User user;
    private final SimpMessagingTemplate template;
    private final SessionService sessionService;
    private final KafkaChatProducer kafkaChatProducer;
    private final UserRepository userRepository;

    public void sendChatMessage(ChatMessagePublish chatMessagePublish) {
        // FIXME : 테스트 필요
        String userSid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String userName = userRepository.findById(userSid)
                .orElseThrow(() -> new RuntimeException("유저 없음"))
                .getUsername();

//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username;
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails)principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//        System.out.println(username);

        ChatMessageSubscribe chatMessageSubscribe = ChatMessageSubscribe.from(chatMessagePublish, userName);

        kafkaChatProducer.sendMessage(chatMessageSubscribe);
    }

    public void join(ChatMessagePublish chatMessagePublish) {
        // FIXME : 테스트 필요
        String userSid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String userName = userRepository.findById(userSid)
                .orElseThrow(() -> new RuntimeException("유저 없음"))
                .getUsername();

        ChatMessageSubscribe chatMessageSubscribe = ChatMessageSubscribe.from(chatMessagePublish, userName);
        chatMessageSubscribe.setContent(userName + " 님이 입장하셨습니다.");

        kafkaChatProducer.sendMessage(chatMessageSubscribe);
    }

    public void leave(ChatMessagePublish chatMessagePublish) throws OpenViduJavaClientException, OpenViduHttpException {
        // FIXME : 테스트 필요
        String userSid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String userName = userRepository.findById(userSid)
                .orElseThrow(() -> new RuntimeException("유저 없음"))
                .getUsername();

        ChatMessageSubscribe chatMessageSubscribe = ChatMessageSubscribe.from(chatMessagePublish, userName);
        chatMessageSubscribe.setContent(userName + " 님이 퇴장하셨습니다.");

        kafkaChatProducer.sendMessage(chatMessageSubscribe);
        sessionService.removeUserFromSession(chatMessageSubscribe.getSessionSid(), user.get_id());
    }

}
