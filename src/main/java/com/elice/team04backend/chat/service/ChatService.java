package com.elice.team04backend.chat.service;


import com.elice.team04backend.chat.entity.Message;

public interface ChatService {
    Iterable<Message> readChat(int issueId, int userId, String userName);
}
