package com.neeko.map.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private Long tagId;
    private Double lng;
    private Double lat;
    private Double radius;
}
