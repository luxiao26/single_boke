package com.huanf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.constants.SystemCanstants;
import com.huanf.domain.Article;
import com.huanf.domain.ArticleTag;
import com.huanf.domain.Category;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.Tag;
import com.huanf.dto.AddArticleDto;
import com.huanf.dto.ArticleDto;
import com.huanf.mapper.ArticleMapper;
import com.huanf.mapper.CategoryMapper;
import com.huanf.service.ArticleService;
import com.huanf.service.ArticleTagService;
import com.huanf.service.ArticleVoService;
import com.huanf.service.CategoryService;
import com.huanf.service.TagService;
import com.huanf.utils.BeanCopyUtils;
import com.huanf.utils.RedisCache;
import com.huanf.vo.ArticleByIdVo;
import com.huanf.vo.ArticleDetailVo;
import com.huanf.vo.ArticleListVo;
import com.huanf.vo.ArticleVo;
import com.huanf.vo.HotArticleVO;
import com.huanf.vo.PageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 35238
 * @date 2023/7/18 0018 21:19
 */
@Service
//ServiceImpl是mybatisPlus官方提供的
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    //操作数据库。ArticleService是我们在huanf-framework工程写的接口
    private ArticleService articleService;


    @Override
    public ResponseResult hotArticleList() {

        //-------------------每调用这个方法就从redis查询文章的浏览量，展示在热门文章列表------------------------

        //获取redis中的浏览量，注意得到的viewCountMap是HashMap双列集合
        Map<String, Integer> viewCountMap = redisCache.getCacheMap("article:viewCount");
        //让双列集合调用entrySet方法即可转为单列集合，然后才能调用stream方法
        List<Article> xxarticles = viewCountMap.entrySet()
                .stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
                //把最终数据转为List集合
                .collect(Collectors.toList());
        //把获取到的浏览量更新到mysql数据库中。updateBatchById是mybatisplus提供的批量操作数据的接口
        articleService.updateBatchById(xxarticles);

        //-----------------------------------------------------------------------------------------

        //查询热门文章，封装成ResponseResult返回。把所有查询条件写在queryWrapper里面
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        //查询的不能是草稿。也就是Status字段不能是1
        queryWrapper.eq(Article::getStatus, SystemCanstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行排序。也就是根据ViewCount字段降序排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只能查询出来10条消息。当前显示第一页的数据，每页显示10条数据
        Page<Article> page = new Page<>(SystemCanstants.ARTICLE_STATUS_CURRENT,SystemCanstants.ARTICLE_STATUS_SIZE);
        page(page,queryWrapper);

        //获取最终的查询结果，把结果封装在Article实体类里面会有很多不需要的字段
        List<Article> articles = page.getRecords();

        //解决: 把结果封装在HotArticleVo实体类里面，在HotArticleVo实体类只写我们要的字段
        //List<HotArticleVO> articleVos = new ArrayList<>();
        //for (Article xxarticle : articles) {
        //    HotArticleVO xxvo = new HotArticleVO();
        //    //使用spring提供的BeanUtils类，来实现bean拷贝。第一个参数是源数据，第二个参数是目标数据，把源数据拷贝给目标数据
        //    BeanUtils.copyProperties(xxarticle,xxvo); //xxarticle就是Article实体类，xxvo就是HotArticleVo实体类
        //    //把我们要的数据封装成集合
        //    articleVos.add(xxvo);
        //}
        //注释掉，使用我们定义的BeanCopyUtils工具类的copyBeanList方法。如下

        //一行搞定
        List<HotArticleVO> articleVos = BeanCopyUtils.copyBeanList(articles, HotArticleVO.class);

        return ResponseResult.okResult(articleVos);
    }

    //----------------------------------分页查询文章的列表---------------------------------

    @Autowired
    //注入我们写的CategoryService接口
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {

        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //判空。如果前端传了categoryId这个参数，那么查询时要和传入的相同。第二个参数是数据表的文章id，第三个字段是前端传来的文章id
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0,Article::getCategoryId,categoryId);

        //只查询状态是正式发布的文章。Article实体类的status字段跟0作比较，一样就表示是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus,SystemCanstants.ARTICLE_STATUS_NORMAL);

        //对isTop字段进行降序排序，实现置顶的文章(isTop值为1)在最前面
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);

        /**
         * 解决categoryName字段没有返回值的问题。在分页之后，封装成ArticleListVo之前，进行处理。第一种方式，用for循环遍历的方式
         */
        ////用categoryId来查询categoryName(category表的name字段)，也就是查询'分类名称'
        //List<Article> articles = page.getRecords();
        //for (Article article : articles) {
        //    //'article.getCategoryId()'表示从article表获取category_id字段，然后作为查询category表的name字段
        //    Category category = categoryService.getById(article.getCategoryId());
        //    //把查询出来的category表的name字段值，也就是article，设置给Article实体类的categoryName成员变量
        //    article.setCategoryName(category.getName());
        //
        //}

        /**
         * 解决categoryName字段没有返回值的问题。在分页之后，封装成ArticleListVo之前，进行处理。第二种方式，用stream流的方式
         */
        //用categoryId来查询categoryName(category表的name字段)，也就是查询'分类名称'
        List<Article> articles = page.getRecords();

        articles = articles.stream()
                .filter(article -> {
                    Long cId = article.getCategoryId();
                    LambdaQueryWrapper<Category> w = new LambdaQueryWrapper<>();
                    w.eq(Category::getId, cId);
                    w.eq(Category::getStatus, "0");
                    List<Category> temp = categoryService.list(w);
                    if (temp.size()==0) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        articles.stream()
                .map(new Function<Article, Article>() {
                    @Override
                    public Article apply(Article article) {
                        //'article.getCategoryId()'表示从article表获取category_id字段，然后作为查询category表的name字段
                        Category category = categoryService.getById(article.getCategoryId());

                        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,article.getId());

                        List<ArticleTag> art = articleTagService.list(articleTagLambdaQueryWrapper);
                        ArrayList<String> tagNameList = new ArrayList<>();
                        for (ArticleTag one : art) {
                            Long tagId = one.getTagId();
                            LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            tagLambdaQueryWrapper.eq(Tag::getDelFlag,"0");
                            tagLambdaQueryWrapper.eq(Tag::getId, tagId);
                            Tag tag = tagService.getOne(tagLambdaQueryWrapper);
                            if (null != tag) {
                                String tagName = tag.getName();
                                tagNameList.add(tagName);
                            }
                        }
                        article.setTagNameList(tagNameList);

                        String name = category.getName();
                        //把查询出来的category表的name字段值，也就是article，设置给Article实体类的categoryName成员变量
                        article.setCategoryName(name);
                        //把查询出来的category表的name字段值，也就是article，设置给Article实体类的categoryName成员变量
                        return article;
                    }
                })
                .collect(Collectors.toList());


        //把最后的查询结果封装成ArticleListVo(我们写的实体类)。BeanCopyUtils是我们写的工具类
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(articles, ArticleListVo.class);


        //把上面那行的查询结果和文章总数封装在PageVo(我们写的实体类)
        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    //---------------------------------根据id查询文章详情----------------------------------

    @Override
    public ResponseResult getArticleDetail(Long id) {

        //根据id查询文章
        Article article = getById(id);

        //-------------------从redis查询文章的浏览量，展示在文章详情---------------------------

        //从redis查询文章浏览量
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        if (viewCount==null || viewCount.equals(0)) {
            viewCount=0;
        }
        article.setViewCount(viewCount.longValue());

        //-----------------------------------------------------------------------------

        //把最后的查询结果封装成ArticleListVo(我们写的实体类)。BeanCopyUtils是我们写的工具类
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);

        //根据分类id，来查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        //如果根据分类id查询的到分类名，那么就把查询到的值设置给ArticleDetailVo实体类的categoryName字段
        if(category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }

        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,id);

        List<ArticleTag> art = articleTagService.list(articleTagLambdaQueryWrapper);
        ArrayList<String> tagNameList = new ArrayList<>();
        for (ArticleTag one : art) {
            Long tagId = one.getTagId();
            LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tagLambdaQueryWrapper.eq(Tag::getDelFlag,"0");
            tagLambdaQueryWrapper.eq(Tag::getId, tagId);
            Tag tag = tagService.getOne(tagLambdaQueryWrapper);
            if (null != tag) {
                String tagName = tag.getName();
                tagNameList.add(tagName);
            }
        }
        articleDetailVo.setTagNameList(tagNameList);

        //封装响应返回。ResponseResult是我们在huanf-framework工程的domain目录写的实体类
        return ResponseResult.okResult(articleDetailVo);
    }

    //--------------------------------根据文章id从mysql查询文章----------------------------

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中的浏览量，对应文章id的viewCount浏览量。article:viewCount是ViewCountRunner类里面写的
        //用户每从mysql根据文章id查询一次浏览量，那么redis的浏览量就增加1
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    //-------------------------------------增加博客文章-----------------------------------

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private ArticleVoService articleVoService;

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        //添加 博客
        ArticleVo articlevo = BeanCopyUtils.copyBean(articleDto, ArticleVo.class);
        articleVoService.save(articlevo);


        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(articlevo.getId(), tagId))
                .collect(Collectors.toList());

        //添加 博客和标签的关联
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    //---------------------------管理后台(文章管理)-分页查询文章----------------------------

    @Override
    public PageVo selectArticlePage(Article article, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.hasText(article.getTitle()),Article::getTitle, article.getTitle());
        queryWrapper.like(StringUtils.hasText(article.getSummary()),Article::getSummary, article.getSummary());

        Page<Article> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);

        //转换成VO
        List<Article> articles = page.getRecords();

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(articles);
        return pageVo;
    }

    //----------------------------修改文章-①根据文章id查询对应的文章--------------------------------

    @Override
    //①先查询根据文章id查询对应的文章
    public ArticleByIdVo getInfo(Long id) {
        Article article = getById(id);
        //获取关联标签
        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,article.getId());
        List<ArticleTag> articleTags = articleTagService.list(articleTagLambdaQueryWrapper);
        List<Long> tags = articleTags.stream().map(articleTag -> articleTag.getTagId()).collect(Collectors.toList());

        ArticleByIdVo articleVo = BeanCopyUtils.copyBean(article,ArticleByIdVo.class);
        articleVo.setTags(tags);
        return articleVo;
    }

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    //②然后才是修改文章
    public void edit(ArticleDto articleDto) {
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        article.setUpdateTime(new Date());
        article.setStatus("0");

        if (article.getIsTop().equals("1")) {
            articleMapper.updateTopCancel();
        }
        //更新博客信息
        updateById(article);
        //删除原有的 标签和博客的关联
        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,article.getId());
        articleTagService.remove(articleTagLambdaQueryWrapper);
        //添加新的博客和标签的关联信息
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(articleDto.getId(), tagId))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
    }

}
