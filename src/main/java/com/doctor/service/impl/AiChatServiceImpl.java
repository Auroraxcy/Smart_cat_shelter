package com.doctor.service.impl;

import com.doctor.dto.ChatSessionDto;
import com.doctor.entity.Cat;
import com.doctor.entity.ChatMessage;
import com.doctor.entity.User;
import com.doctor.repository.CatRepository;
import com.doctor.repository.ChatMessageRepository;
import com.doctor.repository.HealthReminderRepository;
import com.doctor.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Value("${siliconflow.api.key:}")
    private String apiKey;

    @Value("${siliconflow.api.url:https://api.siliconflow.cn/v1/chat/completions}")
    private String apiUrl;

    @Value("${siliconflow.model:Qwen/Qwen2.5-7B-Instruct}")
    private String model;

    @Autowired
    private CatRepository catRepository;

    @Autowired
    private HealthReminderRepository healthReminderRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String chat(String userMessage, String sessionId, User user) {
        String context = buildContext();
        String systemPrompt = "你是一个智能助手，同时了解当前猫舍的数据。\n" +
                "当前猫舍数据供参考：\n" + context +
                "\n你可以回答任何问题，不限于猫舍相关。回答要简洁准确，可以使用 Markdown 格式排版。";

        // 带上本会话历史，让 AI 拥有上下文记忆
        List<ChatMessage> history = sessionId == null ? new ArrayList<>()
                : chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);

        String aiResponse = callSiliconFlow(systemPrompt, history, userMessage);

        ChatMessage msg = new ChatMessage();
        msg.setUser(user);
        msg.setSessionId(sessionId);
        msg.setUserMessage(userMessage);
        msg.setAiResponse(aiResponse);
        chatMessageRepository.save(msg);

        return aiResponse;
    }

    @Override
    public List<ChatSessionDto> getSessions(Long userId) {
        List<String> sessionIds = chatMessageRepository.findSessionIdsByUserId(userId);
        List<ChatSessionDto> sessions = new ArrayList<>();
        for (String sid : sessionIds) {
            if (sid == null) continue;
            List<ChatMessage> msgs = chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(sid);
            if (msgs.isEmpty()) continue;
            ChatSessionDto dto = new ChatSessionDto();
            dto.setSessionId(sid);
            dto.setTitle(buildTitle(msgs.get(0).getUserMessage()));
            dto.setMessageCount(msgs.size());
            dto.setUpdateTime(msgs.get(msgs.size() - 1).getCreateTime());
            sessions.add(dto);
        }
        return sessions;
    }

    @Override
    public List<ChatMessage> getSessionMessages(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
    }

    @Override
    public void deleteSession(String sessionId, Long userId) {
        List<ChatMessage> msgs = chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
        // 只允许删除属于自己的会话
        msgs.removeIf(m -> m.getUser() == null || !m.getUser().getId().equals(userId));
        chatMessageRepository.deleteAll(msgs);
    }

    private String buildTitle(String firstMessage) {
        if (firstMessage == null || firstMessage.isEmpty()) return "新对话";
        String t = firstMessage.trim().replaceAll("\\s+", " ");
        return t.length() > 20 ? t.substring(0, 20) + "..." : t;
    }

    private String buildContext() {
        StringBuilder sb = new StringBuilder();
        List<Cat> cats = catRepository.findAll();
        sb.append("【猫咪列表】共").append(cats.size()).append("只：\n");
        for (Cat cat : cats) {
            sb.append("- ").append(cat.getName())
              .append("，品种：").append(cat.getBreed())
              .append("，年龄：").append(cat.getAge()).append("岁")
              .append("，体重：").append(cat.getWeight()).append("kg")
              .append("，健康状态：").append(cat.getHealthStatus()).append("\n");
        }
        long pendingReminders = healthReminderRepository.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getCompleted())).count();
        sb.append("【待处理健康提醒】").append(pendingReminders).append("条\n");
        return sb.toString();
    }

    private String callSiliconFlow(String systemPrompt, List<ChatMessage> history, String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "请先在 application.properties 中配置 siliconflow.api.key";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        for (ChatMessage h : history) {
            messages.add(Map.of("role", "user", "content", h.getUserMessage()));
            messages.add(Map.of("role", "assistant", "content", h.getAiResponse()));
        }
        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("max_tokens", 1024);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
            Map choices = (Map) ((List) response.getBody().get("choices")).get(0);
            return (String) ((Map) choices.get("message")).get("content");
        } catch (Exception e) {
            return "AI 服务调用失败：" + e.getMessage();
        }
    }
}
