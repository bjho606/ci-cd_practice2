package com.ssafy.meshroom.backend.domain.contents.application;

import com.ssafy.meshroom.backend.domain.contents.dao.ContentsOrderRepository;
import com.ssafy.meshroom.backend.domain.contents.domain.ContentsOrder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ContentsOrderService {
    public final ContentsOrderRepository contentsOrderRepository;

    public void saveContentsOrder (String SessionId, List<String> contents){
        int idx = 1;
        for(String contentId: contents){
            contentsOrderRepository.save(
                    ContentsOrder.builder()
                            .sessionId(SessionId)
                            .contentsId(contentId)
                            .sequence((long)idx++)
                            .build()
            );
        }
    }
}
