package com.neeko.map.controller;

import com.neeko.map.common.Result;
import com.neeko.map.entity.PhotoAlbum;
import com.neeko.map.service.PhotoAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    private PhotoAlbumService albumService;

    @PostMapping
    public Result<PhotoAlbum> createAlbum(@RequestBody PhotoAlbum album) {
        PhotoAlbum result = albumService.createAlbum(
            album.getUserId(),
            album.getTitle(),
            album.getDescription(),
            album.getIsPublic()
        );
        return Result.success("相册创建成功", result);
    }

    @GetMapping("/user/{userId}")
    public Result<List<PhotoAlbum>> getUserAlbums(@PathVariable Long userId) {
        List<PhotoAlbum> result = albumService.getUserAlbums(userId);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<PhotoAlbum> getAlbumById(@PathVariable Long id) {
        PhotoAlbum album = albumService.getAlbumDetail(id);
        return album != null ? Result.success(album) : Result.error("相册不存在");
    }

    @PutMapping("/{id}")
    public Result<PhotoAlbum> updateAlbum(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer isPublic
    ) {
        PhotoAlbum result = albumService.updateAlbum(id, userId, title, description, isPublic);
        return Result.success("更新成功", result);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteAlbum(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        boolean success = albumService.deleteAlbum(id, userId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
