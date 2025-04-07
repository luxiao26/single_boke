package com.huanf.handler.security;

import com.alibaba.fastjson.JSON;
import com.huanf.domain.ResponseResult;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.utils.WebUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 35238
 * @date 2023/7/23 0023 20:43
 */
//自定义授权失败的处理器
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        //输出异常信息
        accessDeniedException.printStackTrace();

        //ResponseResult、AppHttpCodeEnum是我们在huanf-framework工程写的类。重点在下面那行的枚举，就是我们返回给前端的信息
        ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);

        //使用spring提供的JSON工具类，把上一行的result转换成JSON，然后响应给前端。WebUtils是我们写的类
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}
