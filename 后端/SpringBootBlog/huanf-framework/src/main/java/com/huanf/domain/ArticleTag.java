package com.huanf.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * @author 35238
 * @date 2023/8/7 0007 15:28
 */
@TableName(value="sg_article_tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
//新增博客文章
public class ArticleTag implements Serializable {
    private static final long serialVersionUID = 625337492348897098L;

    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 标签id
     */
    private Long tagId;

}