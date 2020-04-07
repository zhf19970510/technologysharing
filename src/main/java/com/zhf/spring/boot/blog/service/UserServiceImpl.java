package com.zhf.spring.boot.blog.service;

import com.zhf.spring.boot.blog.domain.*;
import com.zhf.spring.boot.blog.repository.*;
import com.zhf.spring.boot.blog.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User 服务.
 *
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:55
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Transactional
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void removeUser(Long id) {
        User user = userRepository.findOne(id);
        user.setIsDelete(1);
        user.setExpiredPassword(user.getPassword());
        user.setPassword(StringUtils.randomUUID());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void removeUsersInBatch(List<User> users) {
        userRepository.deleteInBatch(users);
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> listUsersByNameLike(String name, Pageable pageable) {
        // 模糊查询
        name = "%" + name + "%";
        Page<User> users = userRepository.findByNameLike(name, pageable);
        return users;
    }

    @Override
    public Page<User> listUsersByIsDeleteAndNameLike(Integer i, String name, Pageable pageable) {
        // 模糊查询
        name = "%" + name + "%";
        Page<User> users = userRepository.findByIsDeleteAndNameLike(i,name, pageable);
        return users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> listUsersByUsernames(Collection<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

/*    @Override
    public Fans createFans(User user, User principal) {
        Fans fans = new Fans(principal);
        boolean isExit = user.addFans(fans);
        if(isExit){
            throw new IllegalArgumentException("已经关注了");
        }
        User u1 = userRepository.save(user);
        List<Fans> fansList = fansRepository.findByUser(u1);
        Collections.sort(fansList, new Comparator<Fans>() {
            @Override
            public int compare(Fans o1, Fans o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return fansList.get(fansList.size()-1);
    }

    @Override
    public Attention createAttention(User user, User principal) {
        Attention attention = new Attention(user);
        boolean isExit = principal.addAttention(attention);
        if(isExit){
            throw new IllegalArgumentException("已经关注了");
        }
        User u1 = userRepository.save(principal);
        // 把当前attention查出来返回
        List<Attention> attentions = attentionRepository.findByUser(u1);
        // 选择最新一条数据
        Collections.sort(attentions, new Comparator<Attention>() {
            @Override
            public int compare(Attention o1, Attention o2) {
                // 从小到大进行排序
                return o1.getId().compareTo(o2.getId());
            }
        });
        return attentions.get(attentions.size()-1);
    }

    @Override
    public void removeFans(User user, Fans fGET) {
        user.removeFans(fGET);
        userRepository.save(user);
    }

    @Override
    public void removeAttention(User principal ,Attention attention){
        principal.removeAttention(attention);
        userRepository.save(principal);
    }*/

    //    @Override
//    public boolean addFansInfo(Long sessionId, Long attentionId) {
//        User attentionObject = userRepository.findOne(attentionId);
//        User sessionUser = userRepository.findOne(sessionId);
//        if(!sessionUser.getAttentions().contains(attentionObject)) {
//            sessionUser.getAttentions().add(attentionObject);
//
//            User saveUser = userRepository.save(sessionUser);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean deleteFansInfo(Long sessionId, Long attentionId) {
//        User attentionObject = userRepository.findOne(attentionId);
//        User sessionUser = userRepository.findOne(sessionId);
//        if(sessionUser.getAttentions().contains(attentionObject)) {
//            sessionUser.getAttentions().remove(attentionObject);
//            userRepository.save(sessionUser);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public List<User> findAllAttentions(Long sessionId) {
//        User user = userRepository.findOne(sessionId);
//
//        return user.getAttentions();
//    }
//
//    @Override
//    public List<User> findAllFans(Long sessionId) {
//        return userRepository.findAllFansBySessionId(sessionId);
//
//    }
}
