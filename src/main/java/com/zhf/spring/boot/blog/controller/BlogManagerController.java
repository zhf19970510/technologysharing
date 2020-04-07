package com.zhf.spring.boot.blog.controller;

import com.zhf.spring.boot.blog.domain.es.EsBlog;
import com.zhf.spring.boot.blog.service.BlogService;
import com.zhf.spring.boot.blog.service.EsBlogService;
import com.zhf.spring.boot.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 查询所有博客
 * @author ZengHongFa
 * @create 2020/3/8 0008 20:05
 */
@Controller
@RequestMapping("/blogsManage")
public class BlogManagerController {

    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private BlogService blogService;

    @GetMapping
    public ModelAndView listEsBlogs(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "async", required = false) boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            Model model) {
        Page<EsBlog> page = null;
        List<EsBlog> list = null;
        try {
            Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = esBlogService.listNewestEsBlogs(keyword, pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        list = page.getContent();    // 当前所在页面数据列表
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);
        return new ModelAndView(async==true?"blogsManage/list :: #mainContainerRepleace":"blogsManage/list", "blogModel", model);
    }

    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {

        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, ""));
        }

        return  ResponseEntity.ok().body( new Response(true, "处理成功"));
    }
}
