package com.ssafy.meshroom.backend.domain.contents.dto;

import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class GroupState {
    String sessionId;
    Boolean isFinish;
}
