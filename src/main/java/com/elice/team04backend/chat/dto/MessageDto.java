package com.elice.team04backend.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
public class MessageDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("issueId")
    private int issueId;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("content")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime timestamp;

    @JsonProperty("readBy")
    private Set<String> readBy;

    @JsonProperty("readById")
    private Set<String> readById;

    public MessageDto(String id, int userId, int issueId, String sender, String content, LocalDateTime timestamp, Set<String> readBy, Set<String> readById) {
        this.id = id;
        this.userId = userId;
        this.issueId = issueId;
        this.sender = sender;
        this.content = content;
        this.timestamp = convertToKoreaTimeZone(timestamp);
        this.readBy = readBy;
        this.readById = readById;
    }

    private ZonedDateTime convertToKoreaTimeZone(LocalDateTime timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    }
}
