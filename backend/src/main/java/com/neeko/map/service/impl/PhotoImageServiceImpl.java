package com.neeko.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neeko.map.entity.PhotoImage;
import com.neeko.map.mapper.PhotoImageMapper;
import com.neeko.map.service.PhotoImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PhotoImageServiceImpl extends ServiceImpl<PhotoImageMapper, PhotoImage> implements PhotoImageService {

    @Override
    public PhotoImage uploadPhoto(MultipartFile file, Long userId, String description, 
                                  Double latitude, Double longitude, String locationName) {
        String url = saveFile(file);
        String thumbnailUrl = generateThumbnail(url);
        
        PhotoImage photo = new PhotoImage();
        photo.setUserId(userId);
        photo.setUrl(url);
        photo.setThumbnailUrl(thumbnailUrl);
        photo.setDescription(description);
        if (latitude != null) photo.setLatitude(BigDecimal.valueOf(latitude));
        if (longitude != null) photo.setLongitude(BigDecimal.valueOf(longitude));
        photo.setLocationName(locationName);
        photo.setIsPublic(2);
        photo.setCreateTime(LocalDateTime.now());
        
        this.save(photo);
        return photo;
    }

    @Override
    public List<PhotoImage> getPublicPhotos(int page, int size) {
        int p = Math.max(1, page);
        int offset = (p - 1) * size;
        return baseMapper.selectPublicPhotos(offset, size);
    }

    @Override
    public List<PhotoImage> getPhotosByLocation(Double latitude, Double longitude, Double radiusKm) {
        return baseMapper.selectByNearby(latitude, longitude, radiusKm);
    }

    @Override
    public List<PhotoImage> getPhotosByTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.selectByTags(tagIds);
    }

    @Override
    public List<PhotoImage> getPhotosByAlbum(Long albumId) {
        return baseMapper.selectByAlbum(albumId);
    }

    @Override
    public List<PhotoImage> getUserPhotos(Long userId) {
        return baseMapper.selectByUser(userId);
    }

    @Override
    public PhotoImage updatePhotoInfo(Long photoId, PhotoImage photo) {
        PhotoImage existing = this.getById(photoId);
        if (existing == null) {
            throw new RuntimeException("照片不存在");
        }
        
        if (photo.getDescription() != null) existing.setDescription(photo.getDescription());
        if (photo.getLocationName() != null) existing.setLocationName(photo.getLocationName());
        if (photo.getLatitude() != null) existing.setLatitude(photo.getLatitude());
        if (photo.getLongitude() != null) existing.setLongitude(photo.getLongitude());
        if (photo.getIsPublic() != null) existing.setIsPublic(photo.getIsPublic());
        if (photo.getAlbumId() != null) existing.setAlbumId(photo.getAlbumId());
        
        this.updateById(existing);
        return existing;
    }

    @Override
    public boolean deletePhoto(Long photoId, Long userId) {
        PhotoImage photo = this.getById(photoId);
        if (photo == null) throw new RuntimeException("照片不存在");
        if (!photo.getUserId().equals(userId)) throw new RuntimeException("无权删除此照片");
        return this.removeById(photoId);
    }

    private String saveFile(MultipartFile file) {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        return "/uploads/photos/" + filename;
    }

    private String generateThumbnail(String url) {
        return url;
    }
}
