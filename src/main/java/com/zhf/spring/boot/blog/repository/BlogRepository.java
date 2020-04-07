package com.zhf.spring.boot.blog.repository;

import com.zhf.spring.boot.blog.domain.Blog;
import com.zhf.spring.boot.blog.domain.Catalog;
import com.zhf.spring.boot.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.border.TitledBorder;
import java.util.List;

/**
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:51
 */
public interface BlogRepository extends JpaRepository<Blog, Long> {
    /**
     * 根据用户名分页查询用户列表（最新）
     * @param user
     * @param title
     * @param pageable
     * @return
     */
    Page<Blog> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);

    /**
     * 根据用户名分页查询用户列表（最热）
     * @param user
     * @param title
     * @param pageable
     * @return
     */
    Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);

    /**
     * 根据关键字和用户名模糊查询博客
     */
    Page<Blog>findByTitleLikeAndUserOrTagsLikeAndUserOrSummaryLikeAndUserOrderByCreateTimeDesc(String title,User user1,String tags,User user2, String summary,User user,Pageable pageable);

    /**
     * 根据关键字和用户名分页查询用户列表
     * @param title
     * @param user
     * @param tags
     * @param user2
     * @param pageable
     * @return
     */
    Page<Blog> findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(String title, User user, String tags, User user2, Pageable pageable);

    /**
     *
     * @param catalog
     * @param pageable
     * @return
     */
    Page<Blog> findByCatalog(Catalog catalog, Pageable pageable);

    /**
     * 根据catalog查找Blog
     */
    List<Blog> findByCatalog(Catalog catalog);

    /**
     * 根据用户和分类查询博客列表
     * @param user
     * @param catalog
     */
    List<Blog> findByUserAndCatalog(User user, Catalog catalog);



}
