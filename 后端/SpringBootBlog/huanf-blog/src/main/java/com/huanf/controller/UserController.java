package com.huanf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanf.annotation.mySystemlog;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.User;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.exception.SystemException;
import com.huanf.service.UserService;
import com.huanf.utils.SecurityUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 35238
 * @date 2023/7/27 0027 15:18
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户的相关接口文档", description = "我是描述信息")
public class UserController {

    @Autowired
    //UserService是我们在huanf-framework工程写的接口
    private UserService userService;

    @GetMapping("/userInfo")
    @mySystemlog(xxbusinessName = "查询个人信息")//接口描述，用于'日志记录'功能
    public ResponseResult userInfo(){
        //查询个人信息
        return userService.userInfo();
    }

    @PutMapping("userInfo")
    @mySystemlog(xxbusinessName = "更新用户信息")//接口描述，用于'日志记录'功能
    @Transactional
    public ResponseResult updateUserInfo(@RequestBody User user){

        LambdaQueryWrapper<User> check = new LambdaQueryWrapper<>();
        check.eq(User::getNickName,user.getNickName());

        //获取当前用户的用户id。SecurityUtils是我们在huanf-framework工程写的类
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,userId);
        wrapper.eq(User::getStatus,"1");
        User one = userService.getOne(wrapper);
        if (one!=null) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_LOGIN_ERROR);
        }

        if (!user.getPassword().isEmpty() && (user.getPassword().contains(" ") || user.getPassword().trim().isEmpty())) {
            // 字符串只包含空格或包含空格的处理逻辑
            if (user.getPassword().trim().isEmpty()) {
                return ResponseResult.errorResult(233,"新密码不能只有空格");
            } else {
                return ResponseResult.errorResult(233,"新密码不能存在空格");
            }
        }
        if (!user.getPassword().isEmpty() && !user.getPassword().matches("^[a-zA-Z0-9]+$")) {
            return ResponseResult.errorResult(233,"新密码只支持英文、数字两种类型");
        }
        if (!user.getPassword().isEmpty() && user.getPassword().length() < 6) {
            return ResponseResult.errorResult(233,"新密码最少需要6位");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,user.getId());
        User domain = new User();
        domain.setNickName(user.getNickName());
        domain.setUserName(user.getNickName());
        domain.setAvatar(user.getAvatar());
        domain.setPhonenumber(user.getPhonenumber());
        domain.setSex(user.getSex());
        if (!user.getPassword().trim().isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String newPassword = passwordEncoder.encode(user.getPassword());
            domain.setPassword(newPassword);
            userService.update(domain, queryWrapper);
            List<User> list = userService.list(check);
            if (list.size()>1) {
                throw new SystemException(AppHttpCodeEnum.NAME_EXIST);
            }
            return ResponseResult.okResult();
        } else {
            userService.update(domain, queryWrapper);
            List<User> list = userService.list(check);
            if (list.size()>1) {
                throw new SystemException(AppHttpCodeEnum.NAME_EXIST);
            }
            return ResponseResult.okResult();
        }
    }

    @PostMapping("/register")
    @mySystemlog(xxbusinessName = "用户注册")//接口描述，用于'日志记录'功能
    public ResponseResult register(@RequestBody User user){
        //注册功能
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,user.getUserName());
        List<User> nameList = userService.list(queryWrapper);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNickName,user.getNickName());
        List<User> nickNameList = userService.list(wrapper);
        if (!(nameList.size()==0)) {
            return ResponseResult.errorResult(191,"用户名已存在");
        }
        if (!(nickNameList.size()==0)) {
            return ResponseResult.errorResult(191,"昵称已存在");
        }
        return userService.register(user);
    }

}
