package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Authority;
import com.zhf.spring.boot.blog.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Authority 服务.
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:54
 */
@Service
public class AuthorityServiceImpl  implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Authority getAuthorityById(Long id) {
        return authorityRepository.findOne(id);
    }

}
