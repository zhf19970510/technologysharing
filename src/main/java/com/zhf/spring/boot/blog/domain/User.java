package com.zhf.spring.boot.blog.domain;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User 实体
 *
 * @author ZengHongFa
 * @create 2020/3/3 0003 12:46
 */
@Entity // 实体
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @NotEmpty(message = "姓名不能为空")
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 20) // 映射为字段，值不能为空
    private String name;

    @NotEmpty(message = "邮箱不能为空")
    @Size(max = 50, message = "长度不对")
    @Email(message = "邮箱格式不对")
    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @NotEmpty(message = "账号不能为空")
    @Size(min = 3, max = 20, message = "账号长度在3-20个字符")
    @Column(nullable = false, length = 20, unique = true)
    private String username; // 用户账号，用户登录时的唯一标识

    @NotEmpty(message = "密码不能为空")
    @Size(max = 100,message = "密码长度最长100个字符")
    @Column(length = 100)
    private String password; // 登录时密码

    @Column(length = 100)
    private String expiredPassword;  //过期密码

    @Column(length = 200)
    @Size(max = 200,message = "头像链接长度最长200个字符")
    private String avatar; // 头像图片地址


    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;
//    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
//    @JoinTable(name = "fans_attention", joinColumns = @JoinColumn(name = "fans_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "attention_id",referencedColumnName = "id"))
//    private List<User> attentions;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(name = "user_fans",joinColumns = @JoinColumn(name = "userId",referencedColumnName = "id"),inverseJoinColumns = @JoinColumn(name = "fansId",referencedColumnName = "id"))
//    private List<Fans> fans;
//
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(name = "user_attention",joinColumns = @JoinColumn(name = "userId",referencedColumnName = "id"),inverseJoinColumns = @JoinColumn(name = "attentionId",referencedColumnName = "id"))
//    private List<Attention> attentions;

    @Column(name = "fansSize")
    private Integer fansSize = 0;       //粉丝数

    @Column(name = "attentionSize")
    Integer attentionSize = 0;          //关注数

    @Column(length = 2)
    private Integer isDelete;

    protected User() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }

    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //  需将 List<Authority> 转成 List<SimpleGrantedAuthority>，否则前端拿不到角色列表名称
        List<SimpleGrantedAuthority> simpleAuthorities = new ArrayList<>();
        for (GrantedAuthority authority : this.authorities) {
            simpleAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }
        return simpleAuthorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEncodePassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePasswd = encoder.encode(password);
        this.password = encodePasswd;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, username='%s', name='%s', email='%s', password='%s']", id, username, name, email,
                password);
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getExpiredPassword() {
        return expiredPassword;
    }

    public void setExpiredPassword(String expiredPassword) {
        this.expiredPassword = expiredPassword;
    }

//    public List<User> getAttentions() {
//        return attentions;
//    }
//
//    public void setAttentions(List<User> attentions) {
//        this.attentions = attentions;
//    }


//    public List<Fans> getFans() {
//        return fans;
//    }
//
//    public void setFans(List<Fans> fans) {
//        this.fans = fans;
//    }
//
//    public List<Attention> getAttentions() {
//        return attentions;
//    }
//
//    public void setAttentions(List<Attention> attentions) {
//        this.attentions = attentions;
//    }

    public Integer getFansSize() {
        return fansSize;
    }

    public void setFansSize(Integer fansSize) {
        this.fansSize = fansSize;
    }

    public Integer getAttentionSize() {
        return attentionSize;
    }

    public void setAttentionSize(Integer attentionSize) {
        this.attentionSize = attentionSize;
    }


//    public boolean addFans(Fans fan){
//        boolean isExist = false;
//        // 判断重复
//        for(int index = 0; index < this.fans.size();index++){
//            if(this.fans.get(index).getUser().getId().equals(fan.getUser().getId())){
//                isExist = true;
//                break;
//            }
//        }
//        if(!isExist){
//            this.fans.add(fan);
//            this.fansSize = this.fans.size();
//        }
//        return isExist;
//    }
//
//    public boolean addAttention(Attention attention){
//        boolean isExist = false;
//        // 判断重复
//        for(int index = 0; index < this.fans.size();index++){
//            if(this.attentions.get(index).getUser().getId().equals(attention.getUser().getId())){
//                isExist = true;
//                break;
//            }
//        }
//        if(!isExist){
//            this.attentions.add(attention);
//            this.attentionSize = this.attentions.size();
//        }
//        return isExist;
//    }
//
//    public void removeFans(Fans f){
//        for(int index = 0;index < this.fans.size();index++){
//            if(this.fans.get(index).getId().equals(f.getId())){
//                this.fans.remove(index);
//                break;
//            }
//        }
//        if(this.fans !=null && this.fansSize > 0){
//            this.fansSize = this.fans.size();
//        }else {
//            this.fansSize = 0;
//        }
//    }
//
//    public void removeAttention(Attention f){
//        for(int index = 0;index < this.attentions.size();index++){
//            if(this.attentions.get(index).getId().equals(f.getId())){
//                this.attentions.remove(index);
//                break;
//            }
//        }
//        if(this.attentions !=null && this.attentionSize > 0){
//            this.attentionSize = this.attentions.size();
//        }else {
//            this.attentionSize = 0;
//        }
//    }

}
