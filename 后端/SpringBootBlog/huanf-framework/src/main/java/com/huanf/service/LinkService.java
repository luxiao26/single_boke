package com.huanf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.domain.Link;
import com.huanf.domain.ResponseResult;
import com.huanf.vo.PageVo;

/**
 * @author 35238
 * @date 2023/7/22 0022 14:43
 */
public interface LinkService extends IService<Link> {

    //查询友链
    ResponseResult getAllLink();

    //分页查询友链
    PageVo selectLinkPage(Link link, Integer pageNum, Integer pageSize);

}
