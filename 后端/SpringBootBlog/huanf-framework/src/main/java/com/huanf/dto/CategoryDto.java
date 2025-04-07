package com.huanf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 35238
 * @date 2023/8/8 0008 14:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    //分类名
    private String name;
    //描述
    private String description;
    //状态0:正常,1禁用
    private String status;
    //分类id
    private String categoryId;
}
