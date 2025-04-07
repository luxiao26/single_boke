package com.huanf.controller;

import com.huanf.domain.ResponseResult;
import com.huanf.domain.Role;
import com.huanf.dto.ChangeRoleStatusDto;
import com.huanf.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 35238
 * @date 2023/8/10 0010 14:05
 */
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    //------------------------------查询角色列表---------------------------------------
    @GetMapping("/list")
    public ResponseResult list(Role role, Integer pageNum, Integer pageSize) {
        return roleService.selectRolePage(role,pageNum,pageSize);
    }

    //-----------------------------修改角色的状态--------------------------------------

    @PreAuthorize("@ps.hasPermission('system:role:edit')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeRoleStatusDto roleStatusDto){
        Role role = new Role();
        role.setId(roleStatusDto.getRoleId());
        role.setStatus(roleStatusDto.getStatus());
        return ResponseResult.okResult(roleService.updateById(role));
    }

    //-------------------------------新增角色-----------------------------------------

    @PreAuthorize("@ps.hasPermission('system:role:edit')")//权限控制，ps是PermissionService类的bean名称
    @PostMapping
    public ResponseResult add( @RequestBody Role role) {
        roleService.insertRole(role);
        return ResponseResult.okResult();
    }

    //----------------------修改角色-根据角色id查询对应的角色-----------------------------

    @GetMapping(value = "/{roleId}")
    public ResponseResult getInfo(@PathVariable Long roleId) {
        Role role = roleService.getById(roleId);
        return ResponseResult.okResult(role);
    }

    //-------------------------修改角色-保存修改好的角色信息------------------------------

    @PreAuthorize("@ps.hasPermission('system:role:edit')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping
    public ResponseResult edit(@RequestBody Role role) {
        roleService.updateRole(role);
        return ResponseResult.okResult();
    }

    //--------------------------------删除角色---------------------------------------

    @PreAuthorize("@ps.hasPermission('system:role:edit')")//权限控制，ps是PermissionService类的bean名称
    @DeleteMapping()
    public ResponseResult remove(@RequestParam(value = "ids")String ids) {
        if (!ids.contains(",")) {
            roleService.removeById(ids);
        } else {
            String[] idArr = ids.split(",");
            for (String id : idArr) {
                roleService.removeById(id);
            }
        }
        return ResponseResult.okResult();
    }

    //--------------------------------新增用户---------------------------------------

    @GetMapping("/listAllRole")
    //①查询角色列表接口
    public ResponseResult listAllRole(){
        List<Role> roles = roleService.selectRoleAll();
        return ResponseResult.okResult(roles);
    }
}
