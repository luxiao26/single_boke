package com.huanf.handler.security;

import com.alibaba.fastjson.JSON;
import com.huanf.domain.ResponseResult;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 35238
 * @date 2023/7/23 0023 20:34
 */
@Component
//自定义认证失败的处理器。ResponseResult、AppHttpCodeEnum是我们在huanf-framework工程写的类
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        //输出异常信息
        authException.printStackTrace();

        //判断是登录才出现异常(返回'用户名或密码错误')，还是没有登录就访问特定接口才出现的异常(返回'需要登录后访问')，还是其它情况(返回'出现错误')
        ResponseResult result = null;
        if(authException instanceof BadCredentialsException){
            //第一个参数返回的是响应码，AppHttpCodeEnum是我们写的实体类。第二个参数是返回具体的信息
            result = ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_ERROR.getCode(),authException.getMessage());
        } else if(authException instanceof InsufficientAuthenticationException){
            //第一个参数返回的是响应码，AppHttpCodeEnum是我们写的实体类
            result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        } else {
            //第一个参数返回的是响应码，AppHttpCodeEnum是我们写的实体类。第二个参数是返回具体的信息
            result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"认证或授权失败");
        }

        //使用spring提供的JSON工具类，把上一行的result转换成JSON，然后响应给前端。WebUtils是我们写的类
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}
