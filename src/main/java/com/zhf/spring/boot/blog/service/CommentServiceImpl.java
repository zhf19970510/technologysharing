package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Comment;
import com.zhf.spring.boot.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Comment 服务.
 * @author ZengHongFa
 * @create 2020/3/4 0004 15:24
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public void removeComment(Long id) {
        commentRepository.delete(id);
    }
    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findOne(id);
    }

    

}
