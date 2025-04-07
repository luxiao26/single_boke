package com.huanf.runner;

import com.huanf.domain.Article;
import com.huanf.mapper.ArticleMapper;
import com.huanf.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 35238
 * @date 2023/7/31 0031 19:52
 */
@Component
//当项目启动时，就把博客的浏览量(id和viewCount字段)存储到redis中。也叫启动预处理。CommandLineRunner是spring提供的接口
public class ViewCountRunner implements CommandLineRunner {

    @Autowired
    //用于操作redis
    private RedisCache redisCache;

    @Autowired
    //用于操作数据库的article表
    private ArticleMapper articleMapper;

    //@Override
    ////下面的写法是stream流+函数式编程
    //public void run(String... args) throws Exception {
    //    //查询数据库中的博客信息，注意只需要查询id、viewCount字段的博客信息
    //    List<Article> articles = articleMapper.selectList(null);//为null即无条件，表示查询所有
    //    articles.stream()
    //            //由于我们需要key、value的数据，所有可以通过stream流，转为map集合。new Function表示函数式编程
    //            .collect(Collectors.toMap(new Function<Article, Long>() {
    //                @Override
    //                public Long apply(Article article) {
    //                    return article.getId();
    //                }
    //            }, new Function<Article, Integer>() {//new Function表示函数式编程
    //                @Override
    //                public Integer apply(Article article) {
    //                    return article.getViewCount().intValue();
    //                }
    //            }));


    @Override
    //下面的写法是stream流+方法引用+Lambda。我们用这种写法使得代码更简短，跟上面那种写法的效果是一样的
    public void run(String... args) throws Exception {
        //查询数据库中的博客信息，注意只需要查询id、viewCount字段的博客信息
        List<Article> articles = articleMapper.selectList(null);//为null即无条件，表示查询所有
        Map<String, Integer> viewCountMap = articles.stream()
                //由于我们需要key、value的数据，所有可以通过stream流

                //下面toMap方法的第一个参数用了方法引用，第二个参数用了Lambda
                //.collect(Collectors.toMap(Article::getId, article -> article.getViewCount().intValue()));

                //由于上面那行Article::getId返回值是Long类型，而我们需要String类型，为了方便转换类型，我们要写成Lambda表达式
                .collect(Collectors.toMap(article -> article.getId().toString(), article -> article.getViewCount().intValue()));


        //把查询到的id作为key，viewCount作为value，一起存入Redis
        redisCache.setCacheMap("article:viewCount",viewCountMap);
    }
}
