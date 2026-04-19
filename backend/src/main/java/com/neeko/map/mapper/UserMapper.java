package com.neeko.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neeko.map.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>${id}</foreach>")
    List<User> selectFriendsInfo(@Param("ids") List<Long> ids);
    
    @Select("SELECT * FROM sys_user WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR nickname LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%') " +
            "LIMIT 20")
    List<User> searchByKeyword(@Param("keyword") String keyword);
}
