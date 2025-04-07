package com.huanf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.domain.Category;
import com.huanf.domain.ResponseResult;
import com.huanf.vo.CategoryVo;
import com.huanf.vo.PageVo;

import java.util.List;

/**
 * @author 35238
 * @date 2023/7/19 0019 22:41
 */
public interface CategoryService extends IService<Category> {
    //查询文章分类的接口
    ResponseResult getCategoryList();

    //写博客-查询文章分类的接口
    List<CategoryVo> listAllCategory();

    //分页查询分类列表
    PageVo selectCategoryPage(Category category, Integer pageNum, Integer pageSize);
}

