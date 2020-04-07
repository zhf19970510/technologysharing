package com.zhf.spring.boot.blog.repository;

import com.zhf.spring.boot.blog.domain.Relationship;
import com.zhf.spring.boot.blog.domain.RelationshipPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author ZengHongFa
 * @create 2020/4/3 0003 12:26
 */
public interface RelationshipRepository extends JpaRepository<Relationship, RelationshipPK> {
    /**
     * 根据关注者id查找所有记录（查找关注的人的id）
     */
    @Query("select toUserId from Relationship where fromUserId =:fromUserId")
    List<Long> findByFromUserId(@Param("fromUserId") Long fromUserId);
    /**
     * 根据被关注者查找所有记录（查找粉丝的id）
     */
    @Query("select fromUserId from Relationship where toUserId =:toUserId")
    List<Long> findByToUserId(@Param("toUserId") Long toUserId);


//    /**
//     * 查询该用户的互相关注id
//     * @param userId
//     * @return
//     */
//    @Query(value = "SELECT DISTINCT t1.from_user_id FROM (SELECT * FROM relationship WHERE to_user_id = ?1)  AS t1 INNER JOIN (SELECT * FROM relationship WHERE from_user_id = ?1) AS t2 ON t1.from_user_id = t2.to_user_id", nativeQuery = true)
//    List<Long> findFriendsByUserId(Long userId);

    /**
     * 查询关注数
     * @param fromUserId
     * @return
     */
    Integer countByFromUserId(Long fromUserId);
    /**
     * 查询粉丝数
     * @param toUserId
     * @return
     */
    Integer countByToUserId(Long toUserId);
}
