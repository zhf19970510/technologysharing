package com.zhf.spring.boot.blog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * @author ZengHongFa
 * @create 2020/4/2 0002 22:17
 */
@Entity
@IdClass(RelationshipPK.class)
public class Relationship {
    private Long fromUserId;
    private Long toUserId;
    public Relationship(){

    }
    public Relationship(Long fromUserId,Long toUserId){
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    @Id
    @Column(name = "from_user_id",nullable = false)
    public Long getFromUserId() {
        return fromUserId;
    }


    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    @Id
    @Column(name = "to_user_id", nullable = false)
    public Long getToUserId() {
        return toUserId;
    }
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()){ return false;}
        Relationship that = (Relationship) o;
        if (fromUserId != null ? !fromUserId.equals(that.fromUserId) : that.fromUserId != null){ return false;}
        if (toUserId != null ? !toUserId.equals(that.toUserId) : that.toUserId != null){ return false;}
        return true;
    }
    @Override
    public int hashCode() {
        int result = fromUserId != null ? fromUserId.hashCode() : 0;
        result = 31 * result + (toUserId != null ? toUserId.hashCode() : 0);
        return result;
    }
}
