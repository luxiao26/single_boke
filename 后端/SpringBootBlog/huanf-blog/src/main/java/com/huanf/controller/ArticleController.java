package com.huanf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.annotation.mySystemlog;
import com.huanf.domain.Article;
import com.huanf.domain.ArticleTag;
import com.huanf.domain.Category;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.Tag;
import com.huanf.mapper.CategoryMapper;
import com.huanf.service.ArticleService;
import com.huanf.service.ArticleTagService;
import com.huanf.service.TagService;
import com.huanf.vo.ArticleListVo;
import com.huanf.vo.PageVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 35238
 * @date 2023/7/18 0018 21:48
 */
@RestController
@RequestMapping("/article")
@Api(tags = "文章的相关接口文档", description = "我是描述信息")
public class ArticleController {

    @Autowired
    //注入公共模块的ArticleService接口
    private ArticleService articleService;

    @Autowired
    private CategoryMapper categoryMapper;

    //----------------------------------测试mybatisPlus---------------------------------

    @GetMapping("/list")
    //Article是公共模块的实体类
    public List<Article> test(){
        //查询数据库的所有数据
        return articleService.list();
    }

    //----------------------------------测试统一响应格式-----------------------------------

    @GetMapping("/hotArticleList")
    @mySystemlog(xxbusinessName = "查询热门文章")//接口描述，用于'日志记录'功能
    //ResponseResult是huanf-framework工程的domain目录的类
    public ResponseResult hotArticleList(){
        //查询热门文章，封装成ResponseResult返回
        ResponseResult result = articleService.hotArticleList();
        return result;
    }

    //----------------------------------分页查询文章的列表---------------------------------

    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleTagService articleTagService;

    @GetMapping("/articleList")
    @mySystemlog(xxbusinessName = "查询文章列表")//接口描述，用于'日志记录'功能
    //ResponseResult是huanf-framework工程的domain目录的类
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId,@RequestParam(required = false) String search){
        if (search!=""){
            List<ArticleListVo> articleListVo = categoryMapper.serchList(search);
            if (articleListVo.size()!=0) {
                PageVo pageVo = new PageVo(articleListVo,articleListVo.get(0).getTotal());
                List<ArticleListVo> list = pageVo.getRows();
                for (ArticleListVo one : list) {
                    Category category = categoryMapper.selectById(one.getCategoryId());
                    one.setCategoryName(category.getName());
                    one.setCategoryId(category.getId());

                    LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,one.getId());

                    List<ArticleTag> art = articleTagService.list(articleTagLambdaQueryWrapper);
                    ArrayList<String> tagNameList = new ArrayList<>();
                    for (ArticleTag o : art) {
                        Long tagId = o.getTagId();
                        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        tagLambdaQueryWrapper.eq(Tag::getDelFlag,"0");
                        tagLambdaQueryWrapper.eq(Tag::getId, tagId);
                        String tagName = tagService.getOne(tagLambdaQueryWrapper).getName();
                        tagNameList.add(tagName);
                    }
                    one.setTagNameList(tagNameList);

                }

                return ResponseResult.okResult(pageVo);
            }

        }
        ResponseResult responseResult = articleService.articleList(pageNum, pageSize, categoryId);
        return responseResult;
    }

    //------------------------------------查询文章详情------------------------------------

    @GetMapping("/{id}") //路径参数形式的HTTP请求，注意下面那行只有加@PathVariable注解才能接收路径参数形式的HTTP请求
    //ResponseResult是huanf-framework工程的domain目录的类
    @mySystemlog(xxbusinessName = "根据id查询文章")//接口描述，用于'日志记录'功能
    public ResponseResult getArticleDetail(@PathVariable("id") Long id) {//注解里指定的id跟上一行保持一致

        //根据id查询文章详情
        return articleService.getArticleDetail(id);

    }

    //------------------------------------从Redis查询------------------------------------

    @PutMapping("/updateViewCount/{id}")
    @mySystemlog(xxbusinessName = "根据文章id从mysql查询文章")//接口描述，用于'日志记录'功能
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }
}
