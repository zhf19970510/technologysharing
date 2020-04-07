package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Authority;

/**
 * Authority 服务.
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:53
 */
public interface AuthorityService {


    /**
     * 根据id获取 Authority
     * @param id
     * @return
     */
    Authority getAuthorityById(Long id);
}

