package com.zhf.spring.boot.blog.repository;

import com.zhf.spring.boot.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * 用户仓库.
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:52
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名分页查询用户列表
     * @param name
     * @param pageable
     * @return
     */
    Page<User> findByNameLike(String name, Pageable pageable);

    User findByUsername(String username);

    /**
     * 根据名称列表查询
     * @param usernames
     * @return
     */
    List<User> findByUsernameIn(Collection<String> usernames);

    User getUserByEmail(String email);

//    @Query(value = "SELECT fans_id,attention_id FROM fans_attention where attention_id = ?1",nativeQuery = true)
//    List<User> findAllFansBySessionId(Long sessionId);

    /**
     * 根据id集合查询用户，分页查询
     */
    Page<User> findByIdIn(List<Long>ids,Pageable pageable);

    /**
     * 根据id集合查询用户不分页
     */
    List<User> findByIdIn(List<Long> ids);

    Page<User> findByIsDeleteAndNameLike(Integer i, String name, Pageable pageable);
}
