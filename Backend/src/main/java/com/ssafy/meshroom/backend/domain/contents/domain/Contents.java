package com.ssafy.meshroom.backend.domain.contents.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("contents")
public class Contents {
    @Id
    @Indexed(unique=true)
    private String _id;

    String contentTitle;

    String description;

}
