package com.neeko.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neeko.map.entity.PhotoImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PhotoImageMapper extends BaseMapper<PhotoImage> {
    
    @Select("SELECT * FROM photo_image WHERE is_public = 2 ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<PhotoImage> selectPublicPhotos(@Param("offset") int offset, @Param("size") int size);
    
    @Select("SELECT * FROM photo_image WHERE is_public = 2 " +
            "AND (6371 * acos(cos(radians(#{latitude})) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(#{longitude})) + " +
            "sin(radians(#{latitude})) * sin(radians(latitude)))) <= #{radiusKm} " +
            "ORDER BY create_time DESC")
    List<PhotoImage> selectByNearby(@Param("latitude") Double latitude, 
                                    @Param("longitude") Double longitude, 
                                    @Param("radiusKm") Double radiusKm);
    
    @Select("SELECT * FROM photo_image WHERE album_id = #{albumId} ORDER BY create_time DESC")
    List<PhotoImage> selectByAlbum(@Param("albumId") Long albumId);
    
    @Select("SELECT * FROM photo_image WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PhotoImage> selectByUser(@Param("userId") Long userId);

    /**
     * 按标签过滤：tag_ids 为逗号分隔的 ID 列表，命中任意一个即返回。
     */
    @Select("<script>"
            + "SELECT DISTINCT pi.* FROM photo_image pi WHERE pi.is_public = 2 AND ("
            + "<foreach collection='tagIds' item='tid' separator=' OR '>"
            + "FIND_IN_SET(#{tid}, IFNULL(pi.tag_ids, '')) &gt; 0"
            + "</foreach>"
            + ") ORDER BY pi.create_time DESC"
            + "</script>")
    List<PhotoImage> selectByTags(@Param("tagIds") List<Long> tagIds);
}
