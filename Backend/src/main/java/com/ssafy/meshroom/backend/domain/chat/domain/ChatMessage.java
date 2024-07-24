package com.ssafy.meshroom.backend.domain.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatMessages")
public class ChatMessage {
    @Id
    @Indexed(unique=true)
    private String _id;

    private MessageType type;

    private String userSid;
    private String senderName;

    String sessionSid;

    private String content;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant timestamp;
//    private Date timestamp = new Date(System.currentTimeMillis());
}
