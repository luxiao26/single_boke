package com.huanf.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 35238
 * @date 2023/7/22 0022 22:59
 */
@Data
@Accessors(chain = true)
public class UserInfoVo {

    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    private String sex;

    private String email;

    private String password;

    private String phonenumber;

}

