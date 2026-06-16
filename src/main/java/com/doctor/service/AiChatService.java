package com.doctor.service;

import com.doctor.dto.ChatSessionDto;
import com.doctor.entity.ChatMessage;
import com.doctor.entity.User;

import java.util.List;

public interface AiChatService {
    String chat(String message, String sessionId, User user);
    List<ChatSessionDto> getSessions(Long userId);
    List<ChatMessage> getSessionMessages(String sessionId);
    void deleteSession(String sessionId, Long userId);
}
