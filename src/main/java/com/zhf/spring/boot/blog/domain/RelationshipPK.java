package com.zhf.spring.boot.blog.domain;

import com.google.common.hash.HashCode;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author ZengHongFa
 * @create 2020/4/2 0002 22:18
 */
public class RelationshipPK implements Serializable {
    private Long fromUserId;
    private Long toUserId;

    @Column(name = "from_user_id",nullable = false)
    @Id
    public Long getFromUserId(){
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId){
        this.fromUserId = fromUserId;
    }

    @Column(name = "to_user_id", nullable = false)
    @Id
    public Long getToUserId(){
        return toUserId;
    }

    public void setToUserId(Long toUserId){
        this.toUserId = toUserId;
    }

    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o==null || getClass()!=o.getClass()){
            return false;
        }
        RelationshipPK that = (RelationshipPK) o;
        if(fromUserId!=null ? !fromUserId.equals(that.fromUserId):that.fromUserId!=null){
            return false;
        }
        if(toUserId!=null ? !toUserId.equals(that.toUserId):that.toUserId!=null){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int result = fromUserId!=null?fromUserId.hashCode():0;
        result = 31*result + (toUserId!=null?toUserId.hashCode():0);
        return result;
    }
}
