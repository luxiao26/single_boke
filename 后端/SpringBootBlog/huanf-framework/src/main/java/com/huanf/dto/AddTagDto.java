package com.huanf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 35238
 * @date 2023/8/7 0007 13:39
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTagDto {

    //备注
    private String remark;
    //标签名
    private String name;

}
