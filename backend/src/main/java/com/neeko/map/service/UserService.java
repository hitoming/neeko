package com.neeko.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neeko.map.entity.User;

public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    User register(String username, String password, String nickname, String phone, String email);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 获取用户信息
     */
    User getUserInfo(Long userId);
    
    /**
     * 更新最后登录信息
     */
    void updateLoginInfo(Long userId, String ip);
}
