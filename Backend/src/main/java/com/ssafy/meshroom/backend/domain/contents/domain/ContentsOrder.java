package com.ssafy.meshroom.backend.domain.contents.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Builder
@Document("contentsOrder")
public class ContentsOrder {
    @Id
    @Indexed(unique=true)
    private String _id;

    private String sessionId;
    private String contentsId;

    private Long sequence;

}
