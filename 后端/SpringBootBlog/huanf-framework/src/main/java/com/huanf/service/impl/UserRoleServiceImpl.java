package com.huanf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.domain.UserRole;
import com.huanf.mapper.UserRoleMapper;
import com.huanf.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author 35238
 * @date 2023/8/10 0010 20:42
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
