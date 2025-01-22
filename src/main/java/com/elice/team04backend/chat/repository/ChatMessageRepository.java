package com.elice.team04backend.chat.repository;

import com.elice.team04backend.chat.entity.Message;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<Message, String> {
    @Query("{ 'issueId': ?0 }")
    Iterable<Message> findByIssueId(int issueId);
}
