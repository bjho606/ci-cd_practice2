package com.ssafy.meshroom.backend.domain.session.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubSessionInfoResponse {
    private String sessionId;
    private String groupName;
    private Long maxUserCount;
    private Long currentUserCount;
    private List<String> username;
}
