package com.zhf.spring.boot.blog.repository;

import com.zhf.spring.boot.blog.domain.Comment;
import com.zhf.spring.boot.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ZengHongFa
 * @create 2020/3/4 0004 15:13
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUser(User user);
}
