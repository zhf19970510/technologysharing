/*!
 * u main JS.
 *
 */
"use strict";
// DOM 加载完再执行
$(function() {
    var currenturl;
    var _pageSize; // 存储用于搜索
    // 关注\粉丝切换事件
    $(".nav-item .nav-link").click(function() {

        var url = $(this).attr("url");
        currenturl = url;
        // 先移除其他的点击样式，再添加当前的点击样式
        $(".nav-item .nav-link").removeClass("active");
        $(this).addClass("active");

        // 加载其他模块的页面到右侧工作区
        $.ajax({
            url: url,
            contentType : 'application/json',
            data:{
                "async":true,
                "pageIndex":0,
                "pageSize":_pageSize,
            },
            success: function(data){
                $("#mainContainer").html(data);
            },
            error : function() {
                toastr.error("error!");
            }
        })
    });

    // 分页
    $.tbpage("#mainContainer", function (pageIndex, pageSize) {
        $.ajax({
            url: currenturl,
            contentType : 'application/json',
            data:{
                "async":true,
                "pageIndex":pageIndex,
                "pageSize":pageSize,
            },
            success: function(data){
                $("#mainContainer").html(data);
            },
            error : function() {
                toastr.error("error!");
            }
        });
        _pageSize = pageSize;
    });
});