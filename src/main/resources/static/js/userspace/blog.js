/*!
 * blog.html 页面脚本.
 * 
 * @since: 1.0.0 2017-03-26
 * @author Way Lau <https://waylau.com>
 */
"use strict";
//# sourceURL=blog.js

// DOM 加载完再执行
$(function () {
    $.catalog("#catalog", ".post-content");

    // 处理删除博客事件

    $(".blog-content-container").on("click", ".blog-delete-blog", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");


        $.ajax({
            url: blogUrl,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 成功后，重定向
                    window.location = data.body;
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 获取评论列表
    function getCommnet(blogId) {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/comments',
            type: 'GET',
            data: {"blogId": blogId},
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                $("#mainContainer").html(data);

            },
            error: function () {
                toastr.error("error!");
            }
        });
    }

    function getSimilarBlog(blogId) {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: '/blogs/similarBlogs',
            type: 'GET',
            data: {"blogId": blogId},
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                $("#similarBlogsReplace").html(data);

            },
            error: function () {
                toastr.error("error!");
            }
        });
    }

    // 提交评论
    $(".blog-content-container").on("click", "#submitComment", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/comments',
            type: 'POST',
            data: {"blogId": blogId, "commentContent": $('#commentContent').val()},
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 清空评论框
                    $('#commentContent').val('');
                    // 获取评论列表
                    getCommnet(blogId);
                    $("#commentSize").text(parseInt($("#commentSize").text()) + 1);
                } else {
                    if(data.message!=null) {
                        toastr.error(data.message);
                    }else {
                        toastr.error("发布评论失败！");
                        layer.msg("请确定是否登录！",{time:3000,icon:5,shift:6},function () {});
                    }
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });


    //用于定位评论地方
    function goToJys(commentId) {
        $("html,body").animate({scrollTop: $(a[commentId=''+commentId])[0].offset().top}, 500);//定位到《静夜思》
    }

    $(".blog-content-container").on("click", "#replyComment", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        var commentId = $(this).attr("commentId");
        $.ajax({
            url: '/comments/reply/' + commentId,
            type: 'GET',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 添加@某某用户
                    $('#commentContent').val("@"+data.body);
                    setCss();
                    // 获取评论列表
                    // getCommnet(blogId);
                    // $("#commentSize").text(parseInt($("#commentSize").text())+1);
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    $(".blog-content-container").on("click", ".blog-delete-comment", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/comments/' + $(this).attr("commentId") + '?blogId=' + blogId,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 获取评论列表
                    getCommnet(blogId);
                    $("#commentSize").text(parseInt($("#commentSize").text()) - 1);
                } else {
                    toastr.error("删除评论失败！");
                    layer.msg("请确定是否登录！",{time:3000,icon:5,shift:6},function () {});
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 提交点赞
    $(".blog-content-container").on("click", "#submitVote", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/votes',
            type: 'POST',
            data: {"blogId": blogId},
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.info(data.message);
                    // 成功后，重定向
                    window.location = blogUrl;
                    // $("#voteSize").html(parseInt($("#voteSize").html())+1);
                } else {
                    layer.msg("请确定是否登录！",{time:3000,icon:5,shift:6},function () {});
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });


    // 提交关注
    $(".blog-content-container").on("click", "#submitAttention", function () {
        var url = $(this).attr("url");
        var optType = $(this).attr("optType");
        var currentAttention = $(this).attr("currentAttention");
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: url,
            data:{"optType": optType,"currentAttention":currentAttention},
            type: 'POST',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                    //toastr.info(data.message);
                    $("#AttentionDiv").html(data);
            },
            error: function () {
                toastr.error("关注失败!");
                layer.msg("请确定是否登录！",{time:3000,icon:5,shift:6},function () {});
            }

        });
    });


    // 取消关注
    $(".blog-content-container").on("click", "#cancelAttention", function () {
        var url = $(this).attr("url");
        var optType = $(this).attr("optType");
        var currentAttention = $(this).attr("currentAttention");
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: url,
            data:{"optType": optType,"currentAttention":currentAttention},
            type: 'POST',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                //toastr.info(data.message);
                $("#AttentionDiv").html(data);
            },
            error: function () {
                toastr.error("取消关注失败!");
                layer.msg("请确定是否登录！",{time:3000,icon:5,shift:6},function () {});
            }

        });
    });

    // 取消点赞
    $(".blog-content-container").on("click", "#cancelVote", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/votes/' + $(this).attr('voteId') + '?blogId=' + blogId,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.info(data.message);
                    // 成功后，重定向
                    window.location = blogUrl;
                } else {
                    layer.msg("请确定是否登录！",{time:3000,icon:5,shift:6},function () {});
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });


    // 取消关注
/*    $(".blog-content-container").on("click", "#cancelAttention", function () {
        var url = $(this).attr("url");
        var attentionId = $(this).attr("attentionId");
        var fansId = $(this).attr("fansId");
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: url+"?attentionId="+attentionId+"&fansId="+fansId,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                //toastr.info(data.message);
                $("#AttentionDiv").html(data);
            },
            error: function () {
                layer.msg("取消关注失败",{time:3000,icon:5,shift:6},function () {});
            }
        })
    });*/

    // 初始化 博客评论
    getCommnet(blogId);

    // getSimilarBlog(blogId);

    //用于定位光标位置
    function setCss(){
        var sr=document.getElementById("commentContent");

        var len=sr.value.length;
        setSelectionRange(sr,len,len); //将光标定位到文本最后
    }

    function setSelectionRange(input, selectionStart, selectionEnd) {
        if (input.setSelectionRange) {
            input.focus();
            input.setSelectionRange(selectionStart, selectionEnd);
        }
        else if (input.createTextRange) {
            var range = input.createTextRange();
            range.collapse(true);
            range.moveEnd('character', selectionEnd);
            range.moveStart('character', selectionStart);
            range.select();
        }
    }

});