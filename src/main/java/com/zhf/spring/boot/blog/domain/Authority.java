package com.zhf.spring.boot.blog.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * 权限.
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:44
 */
@Entity // 实体
public class Authority implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    protected Authority(){

    }

    public Authority(Long id, String name){
        this.id = id;
        this.name = name;
    }

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @Column(nullable = false) // 映射为字段，值不能为空
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
