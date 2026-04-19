package com.neeko.map.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("photo_album")
public class PhotoAlbum {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private String title;
    
    private String description;
    
    @TableField("cover_url")
    private String coverUrl;
    
    @TableField("is_public")
    private Integer isPublic;
    
    @TableField("photo_count")
    private Integer photoCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
