package com.zhf.spring.boot.blog.controller;

import com.zhf.spring.boot.blog.domain.Authority;
import com.zhf.spring.boot.blog.domain.Catalog;
import com.zhf.spring.boot.blog.domain.User;
import com.zhf.spring.boot.blog.service.AuthorityService;
import com.zhf.spring.boot.blog.service.CatalogService;
import com.zhf.spring.boot.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页控制器.
 *
 * @author ZengHongFa
 * @create 2020/3/3 0003 13:05
 */
@Controller
public class MainController {

    private static final Long ROLE_USER_AUTHORITY_ID = 2L;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/")
    public String root() {
        System.out.println("****************\nhelloworld\n******************");
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/blogs";
    }

    /**
     * 获取登录界面
     *
     * @return
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        User user = new User(null, null, null, null);
        model.addAttribute("user", user);
        return "register";
    }

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String registerUser(User user, Model model) {

        User user1 = userService.getUserByUsername(user.getUsername());
        if (user1 != null) {
            model.addAttribute("user", user);
            model.addAttribute("registerError", true);
            model.addAttribute("errorMsg", "注册失败，账号已存在！");
            return "register";
        }
        User useremail = userService.getUserByEmail(user.getEmail());
        if (useremail != null) {
            model.addAttribute("user", user);
            model.addAttribute("registerError", true);
            model.addAttribute("errorMsg", "注册失败，该邮箱已注册！");
            return "register";
        }

        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
        user.setAuthorities(authorities);
        user.setIsDelete(0);
        userService.saveUser(user);
        User user2 = userService.getUserByUsername(user.getUsername());
        Catalog catalog = new Catalog(user2, "默认");
        catalogService.saveCatalog(catalog);
        return "redirect:/login";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
