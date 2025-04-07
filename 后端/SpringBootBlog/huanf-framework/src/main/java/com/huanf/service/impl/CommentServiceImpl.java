package com.huanf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.constants.SystemCanstants;
import com.huanf.domain.Comment;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.User;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.exception.SystemException;
import com.huanf.mapper.CommentMapper;
import com.huanf.service.CommentService;
import com.huanf.service.UserService;
import com.huanf.utils.BeanCopyUtils;
import com.huanf.vo.CommentVo;
import com.huanf.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 35238
 * @date 2023/7/24 0024 23:08
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService{

    @Autowired
    //根据userid查询用户信息，也就是查username
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Override
    //查询评论区的评论
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        //对articleId进行判断，作用是得到指定的文章。如果是文章评论，才会判断articleId，避免友链评论判断articleId时出现空指针
        queryWrapper.eq(SystemCanstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);

        //对评论区的某条评论的rootID进行判断，如果为-1，就表示是根评论。SystemCanstants是我们写的解决字面值的类
        queryWrapper.eq(Comment::getRootId, SystemCanstants.COMMENT_ROOT);

        //文章的评论，避免查到友链的评论
        queryWrapper.eq(Comment::getType,commentType);

        int count = commentService.count();
        Page<Comment> page  = new Page<>(1,count);
        page(page,queryWrapper);

        int skip = (pageNum - 1) * pageSize;
        List<Comment> sortedComments = page.getRecords().stream()
                .sorted(Comparator.comparing(Comment::getCreateTime).reversed())
                .skip(skip)
                .limit(pageSize)
                .collect(Collectors.toList());
        List<CommentVo> commentVoList = xxToCommentList(sortedComments);

        //遍历(可以用for循环，也可以用stream流)。查询子评论(注意子评论只查到二级评论，不再往深查)
        for (CommentVo commentVo : commentVoList) {
            Long userId = commentVo.getCreateBy();
            User user = userService.getById(userId);
            String avatar = user.getAvatar();
            commentVo.setAvater(avatar);
            //查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());
            //把查到的children子评论集的集合，赋值给commentVo类的children字段
            commentVo.setChildren(children);

        }

        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }

    //在文章的评论区发送评论
    @Override
    public ResponseResult addComment(Comment comment) {
        //注意前端在调用这个发送评论接口时，在请求体是没有向我们传入createTime、createId、updateTime、updateID字段，所以
        //我们这里往后端插入数据时，就会导致上面那行的四个字段没有值
        //为了解决这个问题，我们在huanf-framework工程新增了MyMetaObjectHandler类、修改了Comment类。详细可自己定位去看一下代码

        //限制用户在发送评论时，评论内容不能为空。如果为空就抛出异常
        if(!StringUtils.hasText(comment.getContent())){
            //AppHttpCodeEnum是我们写的枚举类，CONTENT_NOT_NULL代表提示''
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }

        //解决了四个字段没有值的情况，就可以直接调用mybatisplus提供的save方法往数据库插入数据(用户发送的评论的各个字段)了
        save(comment);

        //封装响应返回
        return ResponseResult.okResult();
    }

    //-------------------------------下面是一些方便调用的方法--------------------------------------

    //根据根评论的id，来查询对应的所有子评论(注意子评论只查到二级评论，不再往深查)
    private List<CommentVo> getChildren(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        //对子评论按照时间进行排序
        queryWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        //调用下面那个方法
        List<CommentVo> commentVos = xxToCommentList(comments);
        return commentVos;
    }

    //封装响应返回。CommentVo、BeanCopyUtils、ResponseResult、PageVo是我们写的类
    private List<CommentVo> xxToCommentList(List<Comment> list){
        //获取评论区的所有评论
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历(可以用for循环，也可以用stream流)。由于封装响应好的数据里面没有username字段，所以我们还不能返回给前端。这个遍历就是用来得到username字段
        for (CommentVo commentVo : commentVos) {

            //需要根据commentVo类里面的createBy字段，然后用createBy字段去查询user表的nickname字段(子评论的用户昵称)
            User userIdTemp = userService.getById(commentVo.getCreateBy());
            if (userIdTemp==null) {
                continue;
            }
            String nickName = userIdTemp.getNickName();
            //然后把nickname字段(发这条子评论的用户昵称)的数据赋值给commentVo类的username字段
            commentVo.setUsername(nickName);

            Long userId = commentVo.getCreateBy();
            User user = userService.getById(userId);
            String avatar = user.getAvatar();
            commentVo.setAvater(avatar);

            //查询根评论的用户昵称。怎么判断是根评论的用户呢，判断toCommentId为1，就表示这条评论是根评论
            if(commentVo.getToCommentUserId() != -1){
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                //然后把nickname字段(发这条根评论的用户昵称)的数据赋值给commentVo类的toCommentUserName字段
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }
        commentVos = commentVos.stream()
                .filter(user -> user.getUsername() != null)
                .collect(Collectors.toList());

        //返回给前端
        return commentVos;
    }
}