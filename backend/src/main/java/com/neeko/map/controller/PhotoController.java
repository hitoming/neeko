package com.neeko.map.controller;

import com.neeko.map.common.Result;
import com.neeko.map.entity.PhotoImage;
import com.neeko.map.service.PhotoImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    private PhotoImageService photoImageService;

    private static final String UPLOAD_PATH = "F:/GIS_2026/code/neeko_map/uploads/";

    @PostMapping("/upload")
    public Result<PhotoImage> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "locationName", required = false) String locationName
    ) {
        try {
            String fileName = saveFile(file);
            String url = "/uploads/" + fileName;
            
            PhotoImage result = photoImageService.uploadPhoto(
                file, userId, description, latitude, longitude, locationName
            );
            result.setName(name);
            result.setUrl(url);
            result.setAlbumId(albumId);
            
            return Result.success("上传成功", result);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/public")
    public Result<List<PhotoImage>> getPublicPhotos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        List<PhotoImage> list = photoImageService.getPublicPhotos(page, size);
        return Result.success(list);
    }

    @GetMapping("/user/{userId}")
    public Result<List<PhotoImage>> getUserPhotos(@PathVariable Long userId) {
        List<PhotoImage> list = photoImageService.getUserPhotos(userId);
        return Result.success(list);
    }

    @GetMapping("/album/{albumId}")
    public Result<List<PhotoImage>> getAlbumPhotos(@PathVariable Long albumId) {
        List<PhotoImage> list = photoImageService.getPhotosByAlbum(albumId);
        return Result.success(list);
    }

    @GetMapping("/nearby")
    public Result<List<PhotoImage>> getNearbyPhotos(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") Double radiusKm
    ) {
        List<PhotoImage> list = photoImageService.getPhotosByLocation(latitude, longitude, radiusKm);
        return Result.success(list);
    }

    @PutMapping("/update")
    public Result<PhotoImage> updatePhoto(@RequestBody PhotoImage photo) {
        PhotoImage result = photoImageService.updatePhotoInfo(photo.getId(), photo);
        return Result.success("更新成功", result);
    }

    @DeleteMapping("/{id}")
    public Result<String> deletePhoto(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = photoImageService.deletePhoto(id, userId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result<PhotoImage> getPhotoById(@PathVariable Long id) {
        PhotoImage photo = photoImageService.getById(id);
        return photo != null ? Result.success(photo) : Result.error("照片不存在");
    }

    private String saveFile(MultipartFile file) throws IOException {
        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
            + "_" + UUID.randomUUID().toString().substring(0, 8) + suffix;

        File destFile = new File(uploadDir, fileName);
        file.transferTo(destFile);

        return fileName;
    }
}
