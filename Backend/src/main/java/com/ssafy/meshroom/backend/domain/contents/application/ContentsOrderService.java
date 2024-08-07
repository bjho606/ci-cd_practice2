package com.ssafy.meshroom.backend.domain.contents.application;

import com.ssafy.meshroom.backend.domain.OVToken.application.OVTokenService;
import com.ssafy.meshroom.backend.domain.contents.dao.ContentsOrderRepository;
import com.ssafy.meshroom.backend.domain.contents.dao.ContentsRepository;
import com.ssafy.meshroom.backend.domain.contents.domain.ContentsOrder;
import com.ssafy.meshroom.backend.domain.contents.dto.ContentsOrderSubscribe;
import com.ssafy.meshroom.backend.domain.contents.dto.CurrentGroupState;
import com.ssafy.meshroom.backend.domain.session.dao.SessionRepository;
import com.ssafy.meshroom.backend.domain.session.domain.Session;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ContentsOrderService {

    public final ContentsOrderRepository contentsOrderRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OVTokenService ovTokenService;
    @Autowired
    private final RedisTemplate<String, Boolean> booleanRedisTemplate;
    private final SessionRepository sessionRepository;

    public void saveContentsOrder(String sessionSid, List<String> contents) {
        int idx = 1;
        for (String contentId : contents) {
            contentsOrderRepository.save(
                    ContentsOrder.builder()
                            .sessionId(sessionSid)
                            .contentsId(contentId)
                            .sequence((long) idx++)
                            .isDone(false)
                            .build()
            );

        }

    }

    public Response<ContentsOrderSubscribe> nextContents(boolean isStart) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Session session = ovTokenService.getMainSessionFromUserId(userId);

        List<ContentsOrder> li = contentsOrderRepository.findAllBySessionId(session.get_id());

        Long total = (long) li.size();
        Long sequence = 0L;
        String contentsId = "";

        for (int i = 0; i < li.size(); i++) {
            ContentsOrder con = li.get(i);
            if (isStart) {
                sequence = con.getSequence();
                contentsId = con.getContentsId();
                break;
            }
            if (!con.getIsDone()) {
                contentsOrderRepository.updateById(con.get_id(), true);
                if (i != li.size() - 1) {
                    contentsId = li.get(i + 1).getContentsId();
                    sequence = li.get(i + 1).getSequence();
                } else {
                    contentsId = "null";
                    sequence = -1L;
                }
                break;
            }
        }

        log.info("contentsId {} , sequence {}   ", contentsId, sequence);

        // 모든 세션에 대한 status를 false로 갱신해주는 작업
        HashMap<String, Boolean> groupStates = new HashMap<>();
        for (ContentsOrder con : li) {
            groupStates.put(con.getSessionId(), false);
            booleanRedisTemplate.opsForValue().set(con.getSessionId(), false);
        }

        CurrentGroupState currentGroupState = new CurrentGroupState(groupStates);


        // 수정해야함
        ContentsOrderSubscribe ret = ContentsOrderSubscribe.builder()
                .totalContentsCount(total)
                .contentsId(contentsId)
                .contentsSequence(sequence)
                .currentGroupState(currentGroupState)
                .build();

        simpMessagingTemplate.convertAndSend("/subscribe/contents/" + session.getSessionId(), ret);
        return new Response<ContentsOrderSubscribe>(true, 200L, "ok", ret);
    }


    public Response<ContentsOrderSubscribe> reloadCurrentContent() {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Session session = ovTokenService.getMainSessionFromUserId(userId);
        List<ContentsOrder> contentsOrders = contentsOrderRepository.findAllBySessionId(session.get_id());

        ContentsOrder currentContent = null;

        for (ContentsOrder con : contentsOrders) {
            if (!con.getIsDone()) {
                currentContent = con;
                break;
            }
        }

        //모든 세션에 대한 status를 false로 갱신해주는 작업
        HashMap<String, Boolean> groupStates = new HashMap<>();
        for (ContentsOrder con : contentsOrders) {
            groupStates.put(con.getSessionId(), false);
            booleanRedisTemplate.opsForValue().set(con.getSessionId(), false);
        }

        CurrentGroupState currentGroupState = new CurrentGroupState(groupStates);


        ContentsOrderSubscribe ret = ContentsOrderSubscribe.builder()
                .contentsId(currentContent.getContentsId())
                .contentsSequence(currentContent.getSequence())
                .totalContentsCount((long) contentsOrders.size())
                .currentGroupState(currentGroupState)
                .build();


        simpMessagingTemplate.convertAndSend("/subscribe/contents/" + session.getSessionId(), ret);
        return new Response<>(true, 200L, "ok", ret);
    }

    // 팀장이 본인 해당하는 세션 완료 누르기
    public Response<ContentsOrderSubscribe> finishSubSession(String subSessionId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Session mainSession = ovTokenService.getMainSessionFromUserId(userId);

        booleanRedisTemplate.opsForValue().set(subSessionId, true);

        Optional<List<Session>> optionalSubSessions = sessionRepository.findAllByMainSession(mainSession.getSessionId());

        List<Session> subSessions = optionalSubSessions.get();

        HashMap<String, Boolean> groupStates = new HashMap<>();

        for (Session subSession : subSessions) {
            Boolean state = booleanRedisTemplate.opsForValue().get(subSession.getSessionId());
            groupStates.put(subSession.getSessionId(), state);
        }


        List<ContentsOrder> li = contentsOrderRepository.findAllBySessionId(mainSession.get_id());
        ContentsOrder currentContent = null;
        for (ContentsOrder con : li) {
            if (!con.getIsDone()) {
                currentContent = con;
                break;
            }

        }
        CurrentGroupState currentGroupState = new CurrentGroupState(groupStates);


        ContentsOrderSubscribe ret = ContentsOrderSubscribe.builder()
                .contentsId(currentContent.getContentsId())
                .contentsSequence(currentContent.getSequence())
                .totalContentsCount((long) subSessions.size())
                .currentGroupState(currentGroupState)
                .build();

        simpMessagingTemplate.convertAndSend("/subscribe/contents/" + mainSession.getMainSession(), ret);
        return new Response<>(true, 200L, "ok", ret);
    }


}
