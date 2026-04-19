package com.neeko.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neeko.map.entity.FriendRelation;
import com.neeko.map.entity.User;
import com.neeko.map.mapper.FriendRelationMapper;
import com.neeko.map.mapper.UserMapper;
import com.neeko.map.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendRelationMapper, FriendRelation> implements FriendService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new RuntimeException("不能添加自己为好友");
        }
        
        FriendRelation existing = baseMapper.selectByUsers(userId, friendId);
        if (existing != null) {
            throw new RuntimeException("你们已经是好友或请求已发送");
        }
        
        FriendRelation relation = new FriendRelation();
        relation.setUserId(userId);
        relation.setFriendId(friendId);
        relation.setStatus(0);
        relation.setCreateTime(LocalDateTime.now());
        relation.setUpdateTime(LocalDateTime.now());
        
        this.save(relation);
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        FriendRelation relation = baseMapper.selectByUsers(friendId, userId);
        if (relation == null) {
            throw new RuntimeException("好友请求不存在");
        }
        if (relation.getStatus() != 0) {
            throw new RuntimeException("该请求已被处理");
        }
        
        relation.setStatus(1);
        relation.setUpdateTime(LocalDateTime.now());
        this.updateById(relation);
    }

    @Override
    public void rejectFriend(Long userId, Long friendId) {
        FriendRelation relation = baseMapper.selectByUsers(friendId, userId);
        if (relation == null) {
            throw new RuntimeException("好友请求不存在");
        }
        this.removeById(relation.getId());
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        FriendRelation r1 = baseMapper.selectByUsers(userId, friendId);
        FriendRelation r2 = baseMapper.selectByUsers(friendId, userId);
        
        if (r1 != null) {
            this.removeById(r1.getId());
        }
        if (r2 != null) {
            this.removeById(r2.getId());
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        List<Long> friendIds = baseMapper.selectFriendIds(userId);
        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.selectFriendsInfo(friendIds);
    }

    @Override
    public List<User> getPendingRequests(Long userId) {
        List<Long> friendIds = baseMapper.selectPendingFriendIds(userId);
        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.selectFriendsInfo(friendIds);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userMapper.searchByKeyword(keyword);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        FriendRelation relation = baseMapper.selectByUsers(userId, friendId);
        return relation != null && relation.getStatus() == 1;
    }
}
