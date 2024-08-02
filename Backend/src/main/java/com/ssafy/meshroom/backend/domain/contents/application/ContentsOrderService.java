package com.ssafy.meshroom.backend.domain.contents.application;

import com.ssafy.meshroom.backend.domain.OVToken.application.OVTokenService;
import com.ssafy.meshroom.backend.domain.contents.dao.ContentsOrderRepository;
import com.ssafy.meshroom.backend.domain.contents.domain.ContentsOrder;
import com.ssafy.meshroom.backend.domain.contents.dto.ContentsOrderSubscribe;
import com.ssafy.meshroom.backend.domain.session.domain.Session;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ContentsOrderService {
    public final ContentsOrderRepository contentsOrderRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OVTokenService ovTokenService;

    public void saveContentsOrder (String sessionSid, List<String> contents){
        int idx = 1;
        for(String contentId: contents){
            contentsOrderRepository.save(
                    ContentsOrder.builder()
                            .sessionId(sessionSid)
                            .contentsId(contentId)
                            .sequence((long)idx++)
                            .isDone(false)
                            .build()
            );
        }
    }
    public Response<ContentsOrderSubscribe> nextContents(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Session session = ovTokenService.getMainSessionFromUserId(userId);

        List<ContentsOrder> li = contentsOrderRepository.findAllBySessionId(session.get_id());

        Long total = (long)li.size();
        Long sequence = 0L;
        String contentsId = "";

        boolean flag = false;

        for(ContentsOrder con : li){
            log.info(con.toString());
            if(con.getIsDone()) { continue; }
            if(flag){
                sequence = con.getSequence();
                contentsId = con.getContentsId();
                break;
            }else{
                contentsOrderRepository.updateById(con.get_id(), true);
                sequence = -1L;
                contentsId = "finish";
                flag = true;
            }
        }
        log.info("contentsId {} , sequence {}   ",contentsId,sequence);

        ContentsOrderSubscribe ret = ContentsOrderSubscribe.builder()
                .total(total)
                .contentsId(contentsId)
                .sequence(sequence).build();

        simpMessagingTemplate.convertAndSend("/subscribe/contents/" + session.getSessionId(), ret);
        return new Response<ContentsOrderSubscribe>(true, 200L, "ok", ret);
    }
}
