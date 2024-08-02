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
    private Long sequence;
    private Long total;
}
