package com.neeko.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neeko.map.entity.ChatMessage;
import com.neeko.map.mapper.ChatMessageMapper;
import com.neeko.map.service.ChatMessageService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Override
    public ChatMessage sendMessage(Long senderId, Long receiverId, String content) {
        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        this.save(message);
        return message;
    }

    @Override
    public List<ChatMessage> getChatHistory(Long userId, Long friendId, int limit) {
        return baseMapper.selectChatHistory(userId, friendId, limit);
    }

    @Override
    public List<ChatMessage> getUnreadMessages(Long userId) {
        return baseMapper.selectUnreadByReceiver(userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return baseMapper.countUnreadByReceiver(userId);
    }

    @Override
    public void markAsRead(Long userId, Long friendId) {
        baseMapper.updateReadStatus(userId, friendId);
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        ChatMessage message = this.getById(messageId);
        if (message == null) {
            throw new RuntimeException("消息不存在");
        }
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权删除此消息");
        }
        this.removeById(messageId);
    }
}
