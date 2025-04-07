package com.huanf.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 35238
 * @date 2023/7/19 0019 16:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//返回给前端特定的字段
public class HotArticleVO {

    private Long id;
    //标题
    private String title;
    //访问量
    private Long viewCount;

}
