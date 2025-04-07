package com.huanf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.annotation.mySystemlog;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.User;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.exception.SystemException;
import com.huanf.service.BlogLoginService;
import com.huanf.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 35238
 * @date 2023/7/22 0022 21:31
 */
@RestController
@Api(tags = "用户登录的相关接口文档", description = "我是描述信息")
public class BlogLoginController {

    @Autowired
    //BlogLoginService是我们在service目录写的接口
    private BlogLoginService blogLoginService;

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    @mySystemlog(xxbusinessName = "用户登录功能")//接口描述，用于'日志记录'功能
    //ResponseResult是我们在huanf-framework工程里面写的实体类
    public ResponseResult login(@RequestBody User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,user.getUserName());
        queryWrapper.eq(User::getStatus,"1");
        User one = userService.getOne(queryWrapper);
        if (one!=null) {
            return ResponseResult.errorResult(190,"你已被封禁，请联系管理员解封");
        }

        //如果用户在进行登录时，没有传入'用户名'
        if(!StringUtils.hasText(user.getUserName())){
            // 提示'必须要传用户名'。AppHttpCodeEnum是我们写的枚举类。SystemException是我们写的统一异常处理的类
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return blogLoginService.login(user);
    }

    @PostMapping("/logout")
    @mySystemlog(xxbusinessName = "退出登录功能")//接口描述，用于'日志记录'功能
    public ResponseResult logout(){
        return blogLoginService.logout();
    }
}
