package com.huanf.controller;

import com.huanf.domain.ResponseResult;
import com.huanf.domain.Role;
import com.huanf.domain.User;
import com.huanf.dto.ChangeRoleStatusDto;
import com.huanf.dto.ChangeUserStatusDto;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.exception.SystemException;
import com.huanf.service.RoleService;
import com.huanf.service.UserService;
import com.huanf.utils.SecurityUtils;
import com.huanf.vo.UserInfoAndRoleIdsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 35238
 * @date 2023/8/10 0010 19:59
 */
@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    //--------------------------------查询用户列表-------------------------------------

    @GetMapping("/list")
    public ResponseResult list(User user, Integer pageNum, Integer pageSize) {
        return userService.selectUserPage(user,pageNum,pageSize);
    }

    @PreAuthorize("@ps.hasPermission('system:user:add')")//权限控制，ps是PermissionService类的bean名称
    @PostMapping
    public ResponseResult add(@RequestBody User user) {
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (!userService.checkUserNameUnique(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (!userService.checkPhoneUnique(user)){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        if (!userService.checkEmailUnique(user)){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return userService.addUser(user);
    }

    //--------------------------------删除用户--------------------------------------

    @PreAuthorize("@ps.hasPermission('system:user:remove')")//权限控制，ps是PermissionService类的bean名称
    @DeleteMapping("/{userIds}")
    public ResponseResult remove(@PathVariable List<Long> userIds) {
        if(userIds.contains(SecurityUtils.getUserId())){
            return ResponseResult.errorResult(500,"不能删除当前你正在使用的用户");
        }
        userService.removeByIds(userIds);
        return ResponseResult.okResult();
    }

    //-----------------------修改用户-①根据id查询用户信息-----------------------------

    @Autowired
    private RoleService roleService;

    @GetMapping(value = { "/{userId}" })
    public ResponseResult getUserInfoAndRoleIds(@PathVariable(value = "userId") Long userId) {
        List<Role> roles = roleService.selectRoleAll();
        User user = userService.getById(userId);
        //当前用户所具有的角色id列表
        List<Long> roleIds = roleService.selectRoleIdByUserId(userId);

        UserInfoAndRoleIdsVo vo = new UserInfoAndRoleIdsVo(user,roles,roleIds);
        return ResponseResult.okResult(vo);
    }

    //-------------------------修改用户-②更新用户信息--------------------------------

    @PreAuthorize("@ps.hasPermission('system:user:edit')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping
    public ResponseResult edit(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseResult.okResult();
    }

    //------------------------------修改用户状态信息---------------------------------

    @PreAuthorize("@ps.hasPermission('system:user:edit')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeUserStatusDto userStatusDto){
        User user = new User();
        user.setId(userStatusDto.getUserId());
        user.setStatus(userStatusDto.getStatus());
        boolean b = userService.updateById(user);
        return ResponseResult.okResult();
    }


}