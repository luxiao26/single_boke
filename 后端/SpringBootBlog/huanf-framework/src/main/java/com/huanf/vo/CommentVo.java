package com.huanf.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author 35238
 * @date 2023/7/25 0025 13:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo {

    private Long id;

    //文章id
    private Long articleId;
    //根评论id
    private Long rootId;
    //评论内容
    private String content;
    //所回复的目标评论的userid
    private Long toCommentUserId;
    //所回复的目标评论的userName
    private String toCommentUserName;
    //回复目标评论id
    private Long toCommentId;
    //当前评论的创建人id
    private Long createBy;

    private Date createTime;

    private String avater;

    //评论是谁发的
    private String username;

    //查询子评论
    private List<CommentVo> children;

}
