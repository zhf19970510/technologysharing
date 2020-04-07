package com.zhf.spring.boot.blog.repository;

import com.zhf.spring.boot.blog.domain.Catalog;
import com.zhf.spring.boot.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ZengHongFa
 * @create 2020/3/5 0005 21:52
 */
public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    /**
     * 根据用户查询
     * @param user
     * @return
     */
    List<Catalog> findByUser(User user);

    /**
     * 根据用户和分类名查询
     * @param user
     * @param name
     * @return
     */
    List<Catalog> findByUserAndName(User user, String name);

    /**
     * 根据用户和分类名查找分类
     * @param user
     * @param name
     * @return
     */
    Catalog findCatalogByUserAndName(User user,String name);

    List<Catalog> findByNameLike(String name);
}
