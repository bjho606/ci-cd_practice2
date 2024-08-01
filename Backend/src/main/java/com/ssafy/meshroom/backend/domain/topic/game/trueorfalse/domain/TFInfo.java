package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document("content_TrueFalse")
public class TFInfo {
    @Id
    @Indexed(unique=true)
    private String _id;

    String ovToken;
    String sessionId;
    String truth1;
    String truth2;
    String truth3;
    String truth4;
    String false1;
}
