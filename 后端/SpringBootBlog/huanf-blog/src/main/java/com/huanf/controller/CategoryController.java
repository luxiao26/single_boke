package com.huanf.controller;

import com.huanf.annotation.mySystemlog;
import com.huanf.domain.ResponseResult;
import com.huanf.service.CategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 35238
 * @date 2023/7/20 0020 14:15
 */
@RestController
@RequestMapping("/category")
@Api(tags = "文章分类的相关接口文档", description = "我是描述信息")
public class CategoryController {

    @Autowired
    //CategoryService是我们在huanf-framework工程里面写的接口
    private CategoryService categoryService;

    @GetMapping("/getCategoryList")
    @mySystemlog(xxbusinessName = "查询文章的分类")//接口描述，用于'日志记录'功能
    //ResponseResult是我们在huanf-framework工程里面写的实体类
    public ResponseResult getCategoryList(){
        return categoryService.getCategoryList();
    }

}
