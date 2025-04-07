package com.huanf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.domain.RoleMenu;

/**
 * @author 35238
 * @date 2023/8/10 0010 15:47
 */
public interface RoleMenuService extends IService<RoleMenu> {

    //修改角色-保存修改好的角色信息
    void deleteRoleMenuByRoleId(Long id);

}
