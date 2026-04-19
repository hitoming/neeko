package com.neeko.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neeko.map.entity.FriendRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendRelationMapper extends BaseMapper<FriendRelation> {
    
    @Select("SELECT * FROM friend_relation WHERE user_id = #{userId} AND friend_id = #{friendId} LIMIT 1")
    FriendRelation selectByUsers(@Param("userId") Long userId, @Param("friendId") Long friendId);
    
    @Select("SELECT friend_id FROM friend_relation WHERE user_id = #{userId} AND status = 1")
    List<Long> selectFriendIds(@Param("userId") Long userId);
    
    @Select("SELECT friend_id FROM friend_relation WHERE friend_id = #{userId} AND status = 0")
    List<Long> selectPendingFriendIds(@Param("userId") Long userId);
}
