package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Relationship;
import com.zhf.spring.boot.blog.domain.User;
import com.zhf.spring.boot.blog.repository.RelationshipRepository;
import com.zhf.spring.boot.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZengHongFa
 * @create 2020/4/3 0003 12:56
 */
@Service
public class RelationshipServiceImpl implements RelationshipService{

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<User> listFollows(Long userId, Pageable pageable) {
        List<Long> relationshipList = relationshipRepository.findByFromUserId(userId);
        Page<User> userPage = userRepository.findByIdIn(relationshipList, pageable);
        return userPage;
    }

    @Override
    public Page<User> listFans(Long userId, Pageable pageable) {
        List<Long> relationshipList = relationshipRepository.findByToUserId(userId);
        Page<User> userPage = userRepository.findByIdIn(relationshipList, pageable);
        return userPage;
    }

    /*@Override
    public List<Long> listFriends(Long userId) {
        List<Long> relationshipList = relationshipRepository.findFriendsByUserId(userId);
        return relationshipList;
    }*/

    @Override
    public void saveRelationship(Relationship relationship) {
        // 添加关注
        relationshipRepository.save(relationship);
        //更新双方关注数和粉丝数
        updateFollowSize(relationship.getFromUserId());
        updateFanSize(relationship.getToUserId());
    }

    @Override
    public void removeRelationship(Relationship relationship) {
        //删除关系
        relationshipRepository.delete(relationship);
        //更新双方关注数和粉丝数
        updateFollowSize(relationship.getFromUserId());
        updateFanSize(relationship.getToUserId());
    }

    @Override
    public void updateFollowSize(Long userId) {
        User user = userRepository.findOne(userId);
        user.setAttentionSize(relationshipRepository.countByFromUserId(userId));
        userRepository.save(user);
    }

    @Override
    public void updateFanSize(Long userId) {
        User user = userRepository.findOne(userId);
        user.setFansSize(relationshipRepository.countByToUserId(userId));
        userRepository.save(user);
    }
}
