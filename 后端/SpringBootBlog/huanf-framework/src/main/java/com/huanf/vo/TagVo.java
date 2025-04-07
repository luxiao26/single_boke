package com.huanf.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 35238
 * @date 2023/8/6 0006 19:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagVo {

    private Long id;
    private String name;
    private String remark;

}
