package com.neeko.map.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_message")
public class ChatMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("sender_id")
    private Long senderId;
    
    @TableField("receiver_id")
    private Long receiverId;
    
    private String content;
    
    @TableField("is_read")
    private Integer isRead;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
