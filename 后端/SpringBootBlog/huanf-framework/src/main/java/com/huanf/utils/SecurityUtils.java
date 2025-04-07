package com.huanf.utils;

import com.huanf.domain.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author 35238
 * @date 2023/7/26 0026 20:43
 */

//在'发送评论'功能那里会用到的工具类
public class SecurityUtils {

    /**
     * 获取用户的userid
     **/
    public static LoginUser getLoginUser() {
        return (LoginUser) getAuthentication().getPrincipal();
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 指定userid为1的用户就是网站管理员
     * @return
     */
    public static Boolean isAdmin(){
        Long id = getLoginUser().getUser().getId();
        return id != null && 1L == id;
    }

    /**
     * 测试用户，不具有任何权限
     * @return
     */
    public static Boolean isUser(){
        Long id = getLoginUser().getUser().getId();
        return id != null && 2L == id;
    }


    public static Long getUserId() {
        Long id = getLoginUser().getUser().getId();
        return id;
    }
}
