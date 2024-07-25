package com.ssafy.meshroom.backend.domain.contents.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@Document("contents")
public class Contents {
    @Id
    @Indexed(unique=true)
    private String _id;

    String contentTitle;

    String description;

    String imgUrl;

    Long duration;
}
