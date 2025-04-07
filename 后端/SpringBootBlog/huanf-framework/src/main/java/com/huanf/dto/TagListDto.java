package com.huanf.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 35238
 * @date 2023/8/5 0005 21:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "查询标签的请求参数dto") //这个是关于swagger的注解
public class TagListDto {

    //请求参数。用户可传可不传，这两个参数是给用户在搜索框根据name查询对应的标签，或根据remark查询对应的标签
    private String name;
    private String remark;

}
