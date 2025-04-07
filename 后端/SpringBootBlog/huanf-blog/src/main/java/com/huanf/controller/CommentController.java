package com.huanf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.annotation.mySystemlog;
import com.huanf.constants.SystemCanstants;
import com.huanf.domain.Article;
import com.huanf.domain.Comment;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.User;
import com.huanf.dto.addCommentDto;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.exception.SystemException;
import com.huanf.service.ArticleService;
import com.huanf.service.CommentService;
import com.huanf.service.UserService;
import com.huanf.utils.BeanCopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 35238
 * @date 2023/7/25 0025 13:14
 */
@RestController
@RequestMapping("/comment")
@Api(tags = "评论的相关接口文档", description = "我是描述信息")
public class CommentController {

    @Autowired
    //CommentService是我们在huanf-framework工程写的类
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @GetMapping("commentList")
    @mySystemlog(xxbusinessName = "查询文章评论区的评论")//接口描述，用于'日志记录'功能
    //ResponseResult是我们在huanf-framework工程写的类
    public ResponseResult commentList(Long articleId,Integer pageNum,Integer pageSize){
        //SystemCanstants是我们写的用来解决字面值的常量类，Article_COMMENT代表0
        return commentService.commentList(SystemCanstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }

    @PostMapping
    //在文章的评论区发送评论。ResponseResult是我们在huanf-framework工程写的类
    @mySystemlog(xxbusinessName = "在文章评论区发送评论")//接口描述，用于'日志记录'功能
    public ResponseResult addComment(@RequestBody Comment comment){

        Long userId = comment.getUserId();
        if (userId!=null) {
            User user = userService.getById(userId);
            if (user==null) {
                throw new SystemException(AppHttpCodeEnum.SYSTEM_EXITS_NO);
            }

            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getId,userId);
            wrapper.eq(User::getStatus,"1");
            User one = userService.getOne(wrapper);
            if (one!=null) {
                throw new SystemException(AppHttpCodeEnum.SYSTEM_LOGIN_ERROR);
            }
        }

        Article article = articleService.getById(comment.getArticleId());
        if (article.getIsComment().equals("1")) {
            return commentService.addComment(comment);
        }
        return ResponseResult.errorResult(199,"该文章设置了不可评论");

    }

    //@PostMapping
    ////在文章的评论区发送评论。ResponseResult是我们在huanf-framework工程写的类
    //@mySystemlog(xxbusinessName = "在文章评论区发送评论")//接口描述，用于'日志记录'功能
    //public ResponseResult addComment(@RequestBody addCommentDto addCommentDto){
    //    //把addCommentDto类转换为Comment类类型。BeanCopyUtils是我们在huanf-framework工程写的工具类，可以转换类的类型
    //    Comment comment = BeanCopyUtils.copyBean(addCommentDto, Comment.class);
    //    return commentService.addComment(comment);
    //}

    @GetMapping("/linkCommentList")
    @ApiOperation(value = "友链评论列表",notes = "获取友链评论区的评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "页号"),
            @ApiImplicitParam(name = "pageSize",value = "每页大小")
    })
    //在友链的评论区发送评论。ResponseResult是我们在huanf-framework工程写的类
    @mySystemlog(xxbusinessName = "查询友链评论区的评论")//接口描述，用于'日志记录'功能
    public ResponseResult linkCommentList(Integer pageNum,Integer pageSize){
        //commentService是我们刚刚实现文章的评论区发送评论功能时(当时用的是addComment方法，现在用commentList方法)，写的类
        //SystemCanstants是我们写的用来解决字面值的常量类，Article_LINK代表1
        return commentService.commentList(SystemCanstants.LINK_COMMENT,null,pageNum,pageSize);
    }

}
