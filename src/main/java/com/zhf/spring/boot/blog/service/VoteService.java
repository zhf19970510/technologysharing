package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.Vote;

/**
 * @author ZengHongFa
 * @create 2020/3/5 0005 16:23
 */
public interface VoteService {
    /**
     * 根据id获取 Vote
     * @param id
     * @return
     */
    Vote getVoteById(Long id);
    /**
     * 删除Vote
     * @param id
     * @return
     */
    void removeVote(Long id);
}
