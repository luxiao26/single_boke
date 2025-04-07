package com.huanf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.domain.Comment;
import com.huanf.domain.ResponseResult;

/**
 * @author 35238
 * @date 2023/7/24 0024 23:08
 */
public interface CommentService extends IService<Comment> {

    //查询评论区的评论。增加了commentType参数，用于区分文章的评论区、友链的评论区
    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    //在文章的评论区发送评论
    ResponseResult addComment(Comment comment);
}
