package com.ssafy.meshroom.backend.domain.contents.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentsOrderSubscribe {
    private String contentsId;
    private Long contentsSequence;
    private Long totalContentsCount;
    private Long finishGroupCount;
    private Long totalGroupCount;
    private CurrentGroupState currentGroupState;
}
