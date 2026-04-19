package com.neeko.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neeko.map.entity.ChatMessage;
import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
    
    ChatMessage sendMessage(Long senderId, Long receiverId, String content);
    
    List<ChatMessage> getChatHistory(Long userId, Long friendId, int limit);
    
    List<ChatMessage> getUnreadMessages(Long userId);
    
    int getUnreadCount(Long userId);
    
    void markAsRead(Long userId, Long friendId);
    
    void deleteMessage(Long messageId, Long userId);
}
