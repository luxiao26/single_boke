package com.huanf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 35238
 * @date 2023/8/2 0002 21:02
 */
@SpringBootApplication //把这个类标识为引导类(也叫启动类)
@MapperScan("com.huanf.mapper") //mybatis的配置
public class BlogAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogAdminApplication.class, args);
    }
}
