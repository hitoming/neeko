package com.neeko.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neeko.map.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    List<ChatMessage> selectChatHistory(@Param("userId") Long userId, 
                                        @Param("friendId") Long friendId, 
                                        @Param("limit") int limit);
    
    List<ChatMessage> selectUnreadByReceiver(@Param("userId") Long userId);
    
    int countUnreadByReceiver(@Param("userId") Long userId);
    
    @Update("UPDATE chat_message SET is_read = 1 WHERE receiver_id = #{userId} AND sender_id = #{friendId}")
    void updateReadStatus(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
