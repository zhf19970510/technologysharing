package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Comment;

/**
 * @author ZengHongFa
 * @create 2020/3/4 0004 15:23
 */
public interface CommentService {
    /**
     * 根据id获取 Comment
     * @param id
     * @return
     */
    Comment getCommentById(Long id);
    /**
     * 删除评论
     * @param id
     * @return
     */
    void removeComment(Long id);
}
