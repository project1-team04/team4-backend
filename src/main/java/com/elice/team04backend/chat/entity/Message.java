package com.elice.team04backend.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Document(collection = "messages")
public class Message {
    @JsonProperty("id")
    @Id
    private String id;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("issueId")
    private int issueId;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("content")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("readBy")
    private Set<String> readBy = new HashSet<>();

    @JsonProperty("readById")
    private Set<String> readById = new HashSet<>();
}
