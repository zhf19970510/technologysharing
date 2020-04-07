package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Catalog;
import com.zhf.spring.boot.blog.domain.User;

import java.util.List;

/**
 * Catalog 服务接口.
 * @author ZengHongFa
 * @create 2020/3/5 0005 22:02
 */
public interface CatalogService {
    /**
     * 保存Catalog
     * @param catalog
     * @return
     */
    Catalog saveCatalog(Catalog catalog);

    /**
     * 删除Catalog
     * @param id
     * @return
     */
    void removeCatalog(Long id);

    /**
     * 根据id获取Catalog
     * @param id
     * @return
     */
    Catalog getCatalogById(Long id);

    /**
     * 获取Catalog列表
     * @return
     */
    List<Catalog> listCatalogs(User user);

    Catalog findCatalogByUserAndName(User user,String name);

    Catalog findCatalogById(Long id);
}
