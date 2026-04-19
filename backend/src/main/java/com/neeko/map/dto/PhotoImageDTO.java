package com.neeko.map.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PhotoImageDTO {
    
    private Long id;
    private Long userId;
    private Long albumId;
    private String name;
    private String description;
    private String url;
    private String thumbnailUrl;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String location;
    private String tagIds;
    private Integer privacy;
    private Integer viewCount;
    private Integer likeCount;
    private String createTime;
    private String nickname;
    private String avatar;
    private String albumName;
}
