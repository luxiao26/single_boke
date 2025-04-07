package com.huanf.controller;

import com.huanf.domain.Link;
import com.huanf.domain.ResponseResult;
import com.huanf.service.LinkService;
import com.huanf.vo.PageVo;
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

/**
 * @author 35238
 * @date 2023/8/11 0011 8:46
 */
@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    //-----------------------------分页查询友链---------------------------------

    @GetMapping("/list")
    public ResponseResult list(Link link, Integer pageNum, Integer pageSize) {
        PageVo pageVo = linkService.selectLinkPage(link,pageNum,pageSize);
        return ResponseResult.okResult(pageVo);
    }

    //-------------------------------增加友链----------------------------------

    @PreAuthorize("@ps.hasPermission('content:link:add')")//权限控制，ps是PermissionService类的bean名称
    @PostMapping
    public ResponseResult add(@RequestBody Link link){
        linkService.save(link);
        return ResponseResult.okResult();
    }

    //-------------------------------修改友链---------------------------------

    @GetMapping(value = "/{id}")
    //①根据id查询友链
    public ResponseResult getInfo(@PathVariable(value = "id")Long id){
        Link link = linkService.getById(id);
        return ResponseResult.okResult(link);
    }

    @PreAuthorize("@ps.hasPermission('content:link:edit')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping("/changeLinkStatus")
    //②修改友链状态
    public ResponseResult changeLinkStatus(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }

    @PreAuthorize("@ps.hasPermission('content:link:edit')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping
    //③修改友链
    public ResponseResult edit(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }

    //-------------------------------删除友链---------------------------------

    @PreAuthorize("@ps.hasPermission('content:link:remove')")//权限控制，ps是PermissionService类的bean名称
    @DeleteMapping
    public ResponseResult remove(@RequestParam(value = "ids")String ids) {
        if (!ids.contains(",")) {
            linkService.removeById(ids);
        } else {
            String[] idArr = ids.split(",");
            for (String id : idArr) {
                linkService.removeById(id);
            }
        }
        return ResponseResult.okResult();
    }
}
