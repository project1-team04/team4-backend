package com.elice.team04backend.chat.repository;

import com.elice.team04backend.chat.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<Message, String> {
    List<Message> findByIssueId(int issueId);
}
