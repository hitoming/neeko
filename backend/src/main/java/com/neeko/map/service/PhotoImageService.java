package com.neeko.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neeko.map.entity.PhotoImage;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PhotoImageService extends IService<PhotoImage> {
    
    PhotoImage uploadPhoto(MultipartFile file, Long userId, String description, 
                          Double latitude, Double longitude, String locationName);
    
    List<PhotoImage> getPublicPhotos(int page, int size);
    
    List<PhotoImage> getPhotosByLocation(Double latitude, Double longitude, Double radiusKm);
    
    List<PhotoImage> getPhotosByTags(List<Long> tagIds);
    
    List<PhotoImage> getPhotosByAlbum(Long albumId);
    
    List<PhotoImage> getUserPhotos(Long userId);
    
    PhotoImage updatePhotoInfo(Long photoId, PhotoImage photo);
    
    boolean deletePhoto(Long photoId, Long userId);
}
