package com.neeko.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neeko.map.entity.PhotoAlbum;
import java.util.List;

public interface PhotoAlbumService extends IService<PhotoAlbum> {
    
    PhotoAlbum createAlbum(Long userId, String title, String description, Integer isPublic);
    
    List<PhotoAlbum> getUserAlbums(Long userId);
    
    PhotoAlbum getAlbumDetail(Long albumId);
    
    PhotoAlbum updateAlbum(Long albumId, Long userId, String title, String description, Integer isPublic);
    
    boolean deleteAlbum(Long albumId, Long userId);
    
    void incrementPhotoCount(Long albumId);
    
    void decrementPhotoCount(Long albumId);
}
