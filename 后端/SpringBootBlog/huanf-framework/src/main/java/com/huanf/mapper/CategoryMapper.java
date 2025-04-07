package com.huanf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanf.domain.Category;
import com.huanf.domain.ResponseResult;
import com.huanf.vo.ArticleListVo;

import java.util.List;

/**
 * @author 35238
 * @date 2023/7/19 0019 22:38
 */
public interface CategoryMapper extends BaseMapper<Category> {
    List<ArticleListVo> serchList(String search);
}
