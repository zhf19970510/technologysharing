package com.zhf.spring.boot.blog.repository;

import com.zhf.spring.boot.blog.domain.User;
import com.zhf.spring.boot.blog.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Vote 仓库.
 * @author ZengHongFa
 * @create 2020/3/5 0005 16:22
 */
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByUser(User user);
}
