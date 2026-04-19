package com.neeko.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neeko.map.entity.PhotoAlbum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbum> {
    
    @Select("SELECT * FROM photo_album WHERE user_id = #{userId} ORDER BY update_time DESC")
    List<PhotoAlbum> selectByUser(@Param("userId") Long userId);
    
    @Select("SELECT pa.*, (SELECT COUNT(*) FROM photo_image WHERE album_id = pa.id) as photo_count " +
            "FROM photo_album pa WHERE pa.id = #{albumId}")
    PhotoAlbum selectAlbumDetail(@Param("albumId") Long albumId);
}
