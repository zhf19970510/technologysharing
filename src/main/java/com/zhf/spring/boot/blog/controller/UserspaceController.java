package com.zhf.spring.boot.blog.controller;

import com.zhf.spring.boot.blog.domain.*;
import com.zhf.spring.boot.blog.repository.UserRepository;
import com.zhf.spring.boot.blog.service.BlogService;
import com.zhf.spring.boot.blog.service.CatalogService;
import com.zhf.spring.boot.blog.service.RelationshipService;
import com.zhf.spring.boot.blog.service.UserService;
import com.zhf.spring.boot.blog.util.ConstraintViolationExceptionHandler;
import com.zhf.spring.boot.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 用户主页空间控制器.
 *
 * @author ZengHongFa
 * @create 2020/3/3 0003 14:04
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return "redirect:/u/" + username + "/blogs";
    }

    @GetMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("/userspace/profile", "userModel", model);
    }

    /**
     * 保存个人设置
     *
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public String saveProfile(@PathVariable("username") String username, User user) {
        User originalUser = userService.getUserById(user.getId());
        originalUser.setEmail(user.getEmail());
        originalUser.setName(user.getName());

        // 判断密码是否做了变更
        String rawPassword = originalUser.getPassword();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePasswd = encoder.encode(user.getPassword());
        boolean isMatch = encoder.matches(rawPassword, encodePasswd);
        if (!isMatch) {
            originalUser.setEncodePassword(user.getPassword());
        }

        userService.saveUser(originalUser);
        return "redirect:/u/" + username + "/profile";
    }

    /**
     * 获取编辑头像的界面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView avatar(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("/userspace/avatar", "userModel", model);
    }


    /**
     * 保存头像
     *
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
        String avatarUrl = user.getAvatar();

        User originalUser = userService.getUserById(user.getId());
        originalUser.setAvatar(avatarUrl);
        userService.saveUser(originalUser);

        return ResponseEntity.ok().body(new Response(true, "处理成功", avatarUrl));
    }


    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(@PathVariable("username") String username,
                                   @RequestParam(value = "order", required = false, defaultValue = "new") String order,
                                   @RequestParam(value = "catalog", required = false) Long catalogId,
                                   @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                   @RequestParam(value = "async", required = false) boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                   Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        Page<Blog> page = null;

        if (catalogId != null && catalogId > 0) { // 分类查询
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = blogService.listBlogsByCatalog(catalog, pageable);
            order = "";
        } else if (order.equals("hot")) { // 最热查询
            Sort sort = new Sort(Direction.DESC, "readSize", "commentSize", "voteSize");
            Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
            page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
        } else if (order.equals("new")) { // 最新查询
            Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = blogService.listBlogsByTitleVote(user, keyword, pageable);
        }


        List<Blog> list = page.getContent();    // 当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("catalogId", catalogId);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);
        return (async == true ? "/userspace/u :: #mainContainerRepleace" : "/userspace/u");
    }

    /**
     * 获取博客展示界面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        User principal = null;
        Blog blog = blogService.getBlogById(id);

        // 每次读取，简单的可以认为阅读量增加1次
        blogService.readingIncrease(id);

        // 判断操作用户是否是博客的所有者
        boolean isBlogOwner = false;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && username.equals(principal.getUsername())) {
                isBlogOwner = true;
            }
        }


        // 判断操作用户的点赞情况

        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Vote> votes = blog.getVotes();
        Vote currentVote = null; // 当前用户的点赞情况
        boolean currentAttention = false;
        if (principal != null) {
            for (Vote vote : votes) {
                if (vote.getUser().getUsername().equals(principal.getUsername())) {
                    currentVote = vote;
                    break;
                }
            }

            List<User> follows = relationshipService.listFollows(principal.getId(), new PageRequest(0, 10)).getContent();
            for(User u : follows){
                if(u.getId().equals(user.getId())){
                    currentAttention = true;
                    break;
                }
            }
        }

        /*
         * 获取类似博客列表
         */
        Catalog catalog = blog.getCatalog();
        String catalogName = "";
        if (catalog != null) {
            catalogName = catalog.getName();
        }
        List<Blog> blogs = blogService.findTop5SimilarBlogs(id, catalogName);
        model.addAttribute("user",user);
        model.addAttribute("similarBlogs", blogs);
        model.addAttribute("currentAttention",currentAttention);
        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("blogModel", blog);
        model.addAttribute("currentVote", currentVote);

        return "/userspace/blog";
    }


    /**
     * 删除博客
     *
     * @param id
     * @param username
     * @return
     */
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {

        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }


        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    /**
     * 获取新增博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    /**
     * 获取编辑博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        // 获取用户分类列表
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", blogService.getBlogById(id));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    /**
     * 保存博客
     *
     * @param username
     * @param blog
     * @return
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
        // 对 Catalog 进行空处理
        System.out.println(blog);
        System.out.println(blog.getCatalog());
        if (blog.getCatalog().getId() == null) {
            return ResponseEntity.ok().body(new Response(false, "未选择分类"));
        }

        try {
            // 判断是修改还是新增

            if (blog.getId() != null) {
                Blog orignalBlog = blogService.getBlogById(blog.getId());
                orignalBlog.setTitle(blog.getTitle());
                orignalBlog.setContent(blog.getContent());
                orignalBlog.setSummary(blog.getSummary());
                orignalBlog.setCatalog(blog.getCatalog());
                orignalBlog.setTags(blog.getTags());
                blogService.saveBlog(orignalBlog);
            } else {
                User user = (User) userDetailsService.loadUserByUsername(username);
                blog.setUser(user);
                blogService.saveBlog(blog);
            }

        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }
//    @RequestMapping("/addFansInfo")
//    public @ResponseBody String addFansInfo(Long sessionId,Long attentionId){
//        boolean b = userService.addFansInfo(sessionId, attentionId);
//
//        return b?"success":"failure";
//    }
//
//    @RequestMapping("/deleteFansInfo")
//    public @ResponseBody String deleteFansInfo(Long sessionId,Long attentionId){
//        boolean b = userService.deleteFansInfo(sessionId,attentionId);
//        return b?"success":"failure";
//    }
//
//    @RequestMapping("/findAllAttentions")
//    public @ResponseBody List<User> findAllAttentions(Long sessionId){
//        return userService.findAllAttentions(sessionId);
//    }
//
//    @RequestMapping("/findAllFans")
//    public @ResponseBody List<User> findAllFans(Long sessionId)
//    {
//        return userService.findAllFans(sessionId);
//    }

    @GetMapping("/{username}/relation")
    public String getUserRelation(
            @PathVariable("username") String username,
            @RequestParam(value = "async", required = false) boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "searchfor", required = false, defaultValue = "follows") String searchfor, Model model) {
        System.out.println("进来了吗？");
        User user = (User) userDetailsService.loadUserByUsername(username);     // 获取当前博主
        // 判断当前登录用户是否是博主
        // 判断操作用户是否是分类的所有者
        boolean isOwner = false;
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }
        Pageable pageable = new PageRequest(pageIndex, pageSize);
        //List<Long> friendIds = relationshipService.listFriends(user.getId());
        if ("fans".equals(searchfor)) {
            Page<User> userPage1 = relationshipService.listFans(user.getId(), pageable);
            /*List<User> user1List = userPage1.getContent();
            for(int i = 0;i<user1List.size();i++){
                if(friendIds.contains(user1List.get(i).getId())){
                    userPage1.getContent().get(i).setIsFriend(2);
                }
            }*/
            model.addAttribute("page",userPage1);
        } else{
            Page<User> userPage2 = relationshipService.listFollows(user.getId(), pageable);     // 获取的关注者

            /*List<User> user2List = userPage2.getContent();
            for (int i = 0; i < user2List.size(); i++) {
                if(friendIds.contains(user2List.get(i).getId())){
                    userPage2.getContent().get(i).setIsFriend(2);
                }
            }*/
            model.addAttribute("page",userPage2);
        }

        System.out.println(isOwner);
        model.addAttribute("user", user);
        model.addAttribute("searchfor", searchfor);
        model.addAttribute("isBlogOwner", isOwner);
        return (async == true ? "/userspace/urelation :: #mainContainerRepleace" : "/userspace/urelation");
    }

    /**
     * 添加关系
     */
    @PostMapping("/{username}/relation")
    public String followUser(@PathVariable("username") String username, String optType,boolean currentAttention,Model model) throws Exception{
        User user = (User) userDetailsService.loadUserByUsername(username);
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        if (principal == null) {
            throw new Exception("用户未登录，不能取消关注");
        }


        // 判断是关注还是取消关注
        // 关注
        if("follow".equals(optType)){
            relationshipService.saveRelationship(new Relationship(principal.getId(),user.getId()));
            currentAttention = true;
        }else if("notfollow".equals(optType)){
            // 取消关注
            relationshipService.removeRelationship(new Relationship(principal.getId(),user.getId()));
            currentAttention = false;
        }else {
            //非法操作
            throw new Exception("非法操作");
        }
        model.addAttribute("user",user);
        model.addAttribute("currentAttention",currentAttention);
        return "/userspace/blog :: #attentionContainerRepleace";
    }

/*
    @RequestMapping("/{username}/relation/attentionAdd")
    public String attentionAdd(@PathVariable("username") String username, Model model) throws Exception {
        User user = (User) userDetailsService.loadUserByUsername(username);
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        if (principal == null) {
            throw new Exception("用户未登录，不能取消关注");
        }
        Attention attention = null;
        Fans fans = null;
        try {
            fans = userService.createFans(user, principal);
            attention = userService.createAttention(user, principal);
        }catch (Exception e){
            e.printStackTrace();
        }
        model.addAttribute("currentFans",fans);
        model.addAttribute("currentAttention", attention);
        return "/userspace/blog :: #attentionContainerRepleace";
    }


    @DeleteMapping("/{username}/relation/attentionDelete")
    public String attentionDelete(@PathVariable("username") String username,Long attentionId, Long fansId, Model model) throws Exception {
        User user = (User) userDetailsService.loadUserByUsername(username);
        Attention aGET = attentionService.getAttentionById(attentionId);
        Fans fGET = fansService.findFansById(fansId);
        User attentionUser = null;
        if(aGET != null) {
            attentionUser = attentionService.getAttentionById(attentionId).getUser();
        }
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        if (principal == null) {
            throw new Exception("用户未登录，不能取消关注");
        }
        if(!attentionUser.getUsername().equals(principal.getUsername())){
            throw new Exception("不能取消其他人关注");
        }
        // 对关注进行删除操作 比较复杂了
        try{
            //需要做四步
            userService.removeFans(user,fGET);
            fansService.removeFansById(fGET.getId());
            userService.removeAttention(principal,aGET);
            attentionService.removeAttentionById(aGET.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
        model.addAttribute("currentAttention", null);
        return "/userspace/blog :: #attentionContainerRepleace";
    }*/
}
