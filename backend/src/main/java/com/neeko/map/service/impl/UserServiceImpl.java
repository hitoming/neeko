package com.neeko.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neeko.map.entity.User;
import com.neeko.map.mapper.UserMapper;
import com.neeko.map.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /** 与 docs/database.sql 中测试用户（BCrypt）一致；新注册用户也用 BCrypt */
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(String username, String password, String nickname, String phone, String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null && !nickname.isBlank() ? nickname : username);
        user.setPhone(phone != null && !phone.isBlank() ? phone : null);
        user.setEmail(email != null && !email.isBlank() ? email : null);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        
        this.save(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        return "token_" + user.getId() + "_" + System.currentTimeMillis();
    }

    @Override
    public User getUserInfo(Long userId) {
        return this.getById(userId);
    }

    @Override
    public void updateLoginInfo(Long userId, String ip) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        this.updateById(user);
    }
    
    /** 旧数据若为 MD5 32 位十六进制仍可通过 */
    private static String md5Hex(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败");
        }
    }

    private boolean verifyPassword(String inputPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(inputPassword, storedPassword);
        }
        return md5Hex(inputPassword).equalsIgnoreCase(storedPassword);
    }
}
