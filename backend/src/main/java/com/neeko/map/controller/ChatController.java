package com.neeko.map.controller;

import com.neeko.map.common.Result;
import com.neeko.map.entity.ChatMessage;
import com.neeko.map.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @PostMapping("/send")
    public Result<ChatMessage> sendMessage(
            @RequestParam Long receiverId,
            @RequestParam Long senderId,
            @RequestParam String content
    ) {
        ChatMessage message = chatMessageService.sendMessage(senderId, receiverId, content);
        return Result.success("发送成功", message);
    }

    @GetMapping("/history/{friendId}")
    public Result<List<ChatMessage>> getChatHistory(
            @PathVariable Long friendId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<ChatMessage> history = chatMessageService.getChatHistory(userId, friendId, limit);
        return Result.success(history);
    }

    @GetMapping("/unread")
    public Result<Integer> getUnreadCount(@RequestParam Long userId) {
        int count = chatMessageService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PostMapping("/read/{friendId}")
    public Result<String> markAsRead(@PathVariable Long friendId, @RequestParam Long userId) {
        chatMessageService.markAsRead(userId, friendId);
        return Result.success("已标记已读");
    }
}
