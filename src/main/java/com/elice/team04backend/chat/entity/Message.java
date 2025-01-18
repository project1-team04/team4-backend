package com.elice.team04backend.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "messages")
public class Message {
    @JsonProperty("id")
    @Id
    private String id;

    @JsonProperty("issue_id")
    private String issueId;

    @JsonProperty("sender") // JSON의 "sender" 필드와 매핑
    private String sender;

    @JsonProperty("content") // JSON의 "content" 필드와 매핑
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("read_by")
    private Set<String> readBy = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Set<String> getReadBy() {
        return readBy;
    }

    public void setReadBy(Set<String> readBy) {
        this.readBy = readBy;
    }
}
