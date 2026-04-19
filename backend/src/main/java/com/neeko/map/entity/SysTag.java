package com.neeko.map.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_tag")
public class SysTag {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String color;
    
    private String icon;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
