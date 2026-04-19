package com.neeko.map.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("photo_image")
public class PhotoImage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("album_id")
    private Long albumId;
    
    private String name;
    
    private String description;
    
    private String url;
    
    @TableField("thumbnail_url")
    private String thumbnailUrl;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    @TableField("location_name")
    private String locationName;
    
    @TableField("tag_ids")
    private String tagIds;
    
    @TableField("is_public")
    private Integer isPublic;
    
    @TableField("view_count")
    private Integer viewCount;
    
    @TableField("like_count")
    private Integer likeCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
