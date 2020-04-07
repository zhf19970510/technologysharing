package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * User 服务接口.
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:54
 */
public interface UserService {
    /**
     * 保存用户
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    void removeUser(Long id);

    /**
     * 删除列表里面的用户
     * @param users
     * @return
     */
    void removeUsersInBatch(List<User> users);

    /**
     * 更新用户
     * @param user
     * @return
     */
    User updateUser(User user);

    /**
     * 根据id获取用户
     * @param id
     * @return
     */
    User getUserById(Long id);


    /**
     * 根据账号获取用户
     */
    User getUserByUsername(String username);

    /**
     * 获取用户列表
     * @return
     */
    List<User> listUsers();

    /**
     * 根据用户名进行分页模糊查询
     * @param name
     * @param pageable
     * @return
     */
    Page<User> listUsersByNameLike(String name, Pageable pageable);

    /**
     * 根据名称列表查询
     * @param usernames
     * @return
     */
    List<User> listUsersByUsernames(Collection<String> usernames);
    User getUserByEmail(String email);

    Page<User> listUsersByIsDeleteAndNameLike(Integer i, String name, Pageable pageable);

//    boolean addFansInfo(Long sessionId, Long attentionId);
//
//    boolean deleteFansInfo(Long sessionId, Long attentionId);
//
//    List<User> findAllAttentions(Long sessionId);
//
//    List<User> findAllFans(Long sessionId);

//    Fans createFans(User user, User principal);
//
//    Attention createAttention(User user, User principal);
//
//    void removeFans(User user, Fans fGET);
//    public void removeAttention(User principal ,Attention attention);
}
