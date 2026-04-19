package com.neeko.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neeko.map.entity.PhotoAlbum;
import com.neeko.map.mapper.PhotoAlbumMapper;
import com.neeko.map.service.PhotoAlbumService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PhotoAlbumServiceImpl extends ServiceImpl<PhotoAlbumMapper, PhotoAlbum> implements PhotoAlbumService {

    @Override
    public PhotoAlbum createAlbum(Long userId, String title, String description, Integer isPublic) {
        PhotoAlbum album = new PhotoAlbum();
        album.setUserId(userId);
        album.setTitle(title);
        album.setDescription(description);
        album.setIsPublic(isPublic != null ? isPublic : 2); // 默认公开
        album.setPhotoCount(0);
        album.setCreateTime(LocalDateTime.now());
        album.setUpdateTime(LocalDateTime.now());
        
        this.save(album);
        return album;
    }

    @Override
    public List<PhotoAlbum> getUserAlbums(Long userId) {
        return baseMapper.selectByUser(userId);
    }

    @Override
    public PhotoAlbum getAlbumDetail(Long albumId) {
        return baseMapper.selectAlbumDetail(albumId);
    }

    @Override
    public PhotoAlbum updateAlbum(Long albumId, Long userId, String title, String description, Integer isPublic) {
        PhotoAlbum album = this.getById(albumId);
        if (album == null) {
            throw new RuntimeException("相册不存在");
        }
        if (!album.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此相册");
        }
        
        if (title != null) {
            album.setTitle(title);
        }
        if (description != null) {
            album.setDescription(description);
        }
        if (isPublic != null) {
            album.setIsPublic(isPublic);
        }
        album.setUpdateTime(LocalDateTime.now());
        
        this.updateById(album);
        return album;
    }

    @Override
    public boolean deleteAlbum(Long albumId, Long userId) {
        PhotoAlbum album = this.getById(albumId);
        if (album == null) {
            throw new RuntimeException("相册不存在");
        }
        if (!album.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此相册");
        }
        
        // 更新照片数量
        // TODO: 更新用户照片数
        
        return this.removeById(albumId);
    }

    @Override
    public void incrementPhotoCount(Long albumId) {
        PhotoAlbum album = this.getById(albumId);
        if (album != null) {
            album.setPhotoCount(album.getPhotoCount() + 1);
            album.setUpdateTime(LocalDateTime.now());
            this.updateById(album);
        }
    }

    @Override
    public void decrementPhotoCount(Long albumId) {
        PhotoAlbum album = this.getById(albumId);
        if (album != null && album.getPhotoCount() > 0) {
            album.setPhotoCount(album.getPhotoCount() - 1);
            album.setUpdateTime(LocalDateTime.now());
            this.updateById(album);
        }
    }
}
