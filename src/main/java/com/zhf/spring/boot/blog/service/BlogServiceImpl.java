package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.*;
import com.zhf.spring.boot.blog.domain.es.EsBlog;
import com.zhf.spring.boot.blog.repository.BlogRepository;
import com.zhf.spring.boot.blog.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Blog 服务.
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:55
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private CatalogRepository catalogRepository;

    /* (non-Javadoc)
     * @see com.zhf.spring.boot.blog.service.BlogService#saveBlog(com.zhf.spring.boot.blog.domain.Blog)
     */
    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        boolean isNew = (blog.getId() == null);
        EsBlog esBlog = null;

        Blog returnBlog = blogRepository.save(blog);

        if (isNew) {
            esBlog = new EsBlog(returnBlog);
        } else {
            esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
            esBlog.update(returnBlog);
        }

        esBlogService.updateEsBlog(esBlog);
        return returnBlog;
    }

    /* (non-Javadoc)
     * @see com.zhf.spring.boot.blog.service.BlogService#removeBlog(java.lang.Long)
     */
    @Transactional
    @Override
    public void removeBlog(Long id) {
        Blog blog = blogRepository.getOne(id);
        blog.setCatalog(null);
        blog.setComments(null);
        blog.setVotes(null);
        blogRepository.save(blog);
        EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
        blogRepository.delete(id);
        esBlogService.removeEsBlog(esblog.getId());
    }

    /* (non-Javadoc)
     * @see com.zhf.spring.boot.blog.service.BlogService#updateBlog(com.zhf.spring.boot.blog.domain.Blog)
     */
    @Transactional
    @Override
    public Blog updateBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    /* (non-Javadoc)
     * @see com.zhf.spring.boot.blog.service.BlogService#getBlogById(java.lang.Long)
     */
    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.findOne(id);
    }

    @Override
    public Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        String tags = title;
        String summary = title;
        Page<Blog> blogs = blogRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrSummaryLikeAndUserOrderByCreateTimeDesc(title, user, tags, user, summary, user, pageable);
        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
        return blogs;
    }

    @Override
    public void readingIncrease(Long id) {
        Blog blog = blogRepository.findOne(id);
        blog.setReadSize(blog.getReadSize() + 1);
        this.saveBlog(blog);
    }

    @Override
    public void readingDecrease(Long id) {
        Blog blog = blogRepository.findOne(id);
        blog.setReadSize(blog.getReadSize() - 1);
        this.saveBlog(blog);
    }

    @Override
    public Blog createComment(Long blogId, String commentContent) {
        Blog originalBlog = blogRepository.findOne(blogId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = new Comment(user, commentContent);
        originalBlog.addComment(comment);
        return this.saveBlog(originalBlog);
    }

    @Override
    public void removeComment(Long blogId, Long commentId) {
        Blog originalBlog = blogRepository.findOne(blogId);
        originalBlog.removeComment(commentId);
        this.saveBlog(originalBlog);
    }

    @Override
    public Blog createVote(Long blogId) {
        Blog originalBlog = blogRepository.findOne(blogId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Vote vote = new Vote(user);
        boolean isExist = originalBlog.addVote(vote);
        if (isExist) {
            throw new IllegalArgumentException("该用户已经点过赞了");
        }
        return this.saveBlog(originalBlog);
    }

    @Override
    public void removeVote(Long blogId, Long voteId) {
        Blog originalBlog = blogRepository.findOne(blogId);
        originalBlog.removeVote(voteId);
        blogRepository.save(originalBlog);
    }

    @Override
    public Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable) {
        Page<Blog> blogs = blogRepository.findByCatalog(catalog, pageable);
        return blogs;
    }

    @Override
    public List<Blog> findByUserAndCatalog(User user, Catalog catalog) {
        return blogRepository.findByUserAndCatalog(user,catalog);
    }

    @Override
    public void deleteInBatch(List<Blog> blogs) {
        blogRepository.delete(blogs);
    }

    @Override
    public Blog findBlogById(Long id) {
        return blogRepository.findOne(id);
    }

    @Override
    public List<Blog> findTop5SimilarBlogs(Long id,String catalogName) {
        List<Blog> blogs = new ArrayList<>();
        catalogName = "%" + catalogName +"%";
        Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize","createTime");
        Pageable pageable = new PageRequest(0,10, sort);
        List<Catalog> catalogs = catalogRepository.findByNameLike(catalogName);
        for(Catalog catalog: catalogs){
            Page<Blog> blogPage = blogRepository.findByCatalog(catalog, pageable);

            blogs.addAll(blogPage.getContent());
        }

        for(Blog blog:blogs){
            if(id.equals(blog.getId())){
                blogs.remove(blog);
                break;
            }
        }


        if(blogs.size()<=5){
            return blogs;
        }else {
            return blogs.subList(0,5);
        }
    }
}

