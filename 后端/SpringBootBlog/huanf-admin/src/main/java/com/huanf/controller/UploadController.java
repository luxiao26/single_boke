package com.huanf.controller;

import com.huanf.domain.ResponseResult;
import com.huanf.service.OssUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 35238
 * @date 2023/8/7 0007 15:05
 */
@RestController
public class UploadController {

    @Autowired
    private OssUploadService uploadService;

    @PreAuthorize("@ps.hasPermission('content:article:writer')")//权限控制，ps是PermissionService类的bean名称
    @PostMapping("/upload")
    public ResponseResult uploadImg(@RequestParam("img") MultipartFile multipartFile) {
        try {
            return uploadService.uploadImg(multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("不支持该文件类型 -_-");
        }
    }
}