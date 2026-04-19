package com.neeko.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neeko.map.entity.FriendRelation;
import com.neeko.map.entity.User;
import java.util.List;

public interface FriendService extends IService<FriendRelation> {
    
    void addFriend(Long userId, Long friendId);
    
    void confirmFriend(Long userId, Long friendId);
    
    void rejectFriend(Long userId, Long friendId);
    
    void deleteFriend(Long userId, Long friendId);
    
    List<User> getFriends(Long userId);
    
    List<User> getPendingRequests(Long userId);
    
    List<User> searchUsers(String keyword);
    
    boolean isFriend(Long userId, Long friendId);
}
