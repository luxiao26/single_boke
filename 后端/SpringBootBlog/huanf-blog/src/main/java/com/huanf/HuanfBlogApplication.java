package com.huanf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 35238
 * @date 2023/7/18 0018 19:13
 */

@SpringBootApplication
@MapperScan("com.huanf.mapper")
@EnableScheduling//@EnableScheduling是spring提供的定时任务的注解
public class HuanfBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuanfBlogApplication.class,args);
    }

}
