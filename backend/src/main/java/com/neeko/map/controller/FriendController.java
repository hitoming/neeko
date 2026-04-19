package com.neeko.map.controller;

import com.neeko.map.common.Result;
import com.neeko.map.entity.User;
import com.neeko.map.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping("/add")
    public Result<String> addFriend(@RequestParam Long friendId, @RequestParam Long userId) {
        friendService.addFriend(userId, friendId);
        return Result.success("申请已发送");
    }

    @PostMapping("/confirm/{friendId}")
    public Result<String> confirmFriend(@PathVariable Long friendId, @RequestParam Long userId) {
        friendService.confirmFriend(userId, friendId);
        return Result.success("已成为好友");
    }

    @PostMapping("/reject/{friendId}")
    public Result<String> rejectFriend(@PathVariable Long friendId, @RequestParam Long userId) {
        friendService.rejectFriend(userId, friendId);
        return Result.success("已拒绝");
    }

    @DeleteMapping("/{friendId}")
    public Result<String> deleteFriend(@PathVariable Long friendId, @RequestParam Long userId) {
        friendService.deleteFriend(userId, friendId);
        return Result.success("已删除好友");
    }

    @GetMapping("/list")
    public Result<List<User>> getFriendList(@RequestParam Long userId) {
        List<User> friends = friendService.getFriends(userId);
        return Result.success(friends);
    }

    @GetMapping("/pending")
    public Result<List<User>> getPendingRequests(@RequestParam Long userId) {
        List<User> requests = friendService.getPendingRequests(userId);
        return Result.success(requests);
    }

    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> users = friendService.searchUsers(keyword);
        return Result.success(users);
    }
}
