package com.huanf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanf.domain.Article;
import org.springframework.stereotype.Service;

/**
 * @author 35238
 * @date 2023/7/18 0018 21:13
 */
@Service
public interface ArticleMapper extends BaseMapper<Article> {


    void updateTopCancel();

}
