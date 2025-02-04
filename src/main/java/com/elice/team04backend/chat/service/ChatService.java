package com.elice.team04backend.chat.service;


import com.elice.team04backend.chat.dto.MessageDto;
import com.elice.team04backend.chat.entity.Message;

public interface ChatService {
    Iterable<MessageDto> getChat(int issueId);
    Iterable<MessageDto> readChat(int issueId, int userId, String userName);
}
