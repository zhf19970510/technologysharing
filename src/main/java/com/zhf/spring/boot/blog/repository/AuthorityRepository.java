package com.zhf.spring.boot.blog.repository;

import com.zhf.spring.boot.blog.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Authority 仓库.
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:50
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}

