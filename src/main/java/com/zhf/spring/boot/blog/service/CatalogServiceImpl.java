package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Catalog;
import com.zhf.spring.boot.blog.domain.User;
import com.zhf.spring.boot.blog.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Catalog 服务.
 * @author ZengHongFa
 * @create 2020/3/5 0005 22:04
 */
@Service
public class CatalogServiceImpl implements CatalogService{

    @Autowired
    private CatalogRepository catalogRepository;

    @Override
    public Catalog saveCatalog(Catalog catalog) {
        // 判断重复
        List<Catalog> list = catalogRepository.findByUserAndName(catalog.getUser(), catalog.getName());
        if(list !=null && list.size() > 0) {
            throw new IllegalArgumentException("该分类已经存在了");
        }
        return catalogRepository.save(catalog);
    }

    @Transactional
    @Override
    public void removeCatalog(Long id) {
        Catalog catalog = catalogRepository.findOne(id);
        catalogRepository.delete(id);
    }

    @Override
    public Catalog getCatalogById(Long id) {
        return catalogRepository.findOne(id);
    }

    @Override
    public List<Catalog> listCatalogs(User user) {
        return catalogRepository.findByUser(user);
    }

    @Override
    public Catalog findCatalogByUserAndName(User user, String name) {
        Catalog catalog = catalogRepository.findCatalogByUserAndName(user, name);
        return catalog;
    }

    @Override
    public Catalog findCatalogById(Long id) {
        return catalogRepository.findOne(id);
    }
}
