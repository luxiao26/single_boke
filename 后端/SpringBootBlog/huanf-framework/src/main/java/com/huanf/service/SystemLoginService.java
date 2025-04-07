package com.huanf.service;

import com.huanf.domain.ResponseResult;
import com.huanf.domain.User;

/**
 * @author 35238
 * @date 2023/7/22 0022 21:38
 */
public interface SystemLoginService {

    //登录
    ResponseResult login(User user);

    //退出登录
    ResponseResult logout();
}
