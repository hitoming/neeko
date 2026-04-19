package com.neeko.map.controller;

import com.neeko.map.common.Result;
import com.neeko.map.dto.LoginDTO;
import com.neeko.map.dto.RegisterDTO;
import com.neeko.map.entity.User;
import com.neeko.map.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.register(
            dto.getUsername(),
            dto.getPassword(),
            dto.getNickname(),
            dto.getPhone(),
            dto.getEmail()
        );
        return Result.success("注册成功", null);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String token = userService.login(dto.getUsername(), dto.getPassword());
        
        // 更新登录信息
        String ip = getClientIp(request);
        Long userId = extractUserIdFromToken(token);
        if (userId != null) {
            userService.updateLoginInfo(userId, ip);
        }
        
        return Result.success("登录成功", token);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getUserInfo(userId);
        if (user != null) {
            user.setPassword(null); // 隐藏密码
        }
        return Result.success(user);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Long extractUserIdFromToken(String token) {
        // 简化版Token解析，实际应使用JWT
        if (token != null && token.startsWith("token_")) {
            try {
                String[] parts = token.split("_");
                if (parts.length >= 2) {
                    return Long.parseLong(parts[1]);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
