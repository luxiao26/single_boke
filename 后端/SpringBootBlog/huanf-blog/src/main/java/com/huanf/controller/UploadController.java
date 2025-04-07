package com.huanf.controller;

import com.huanf.annotation.mySystemlog;
import com.huanf.domain.ResponseResult;
import com.huanf.service.OssUploadService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 35238
 * @date 2023/7/29 0029 11:17
 */
@RestController
@Api(tags = "文件上传的相关接口文档", description = "我是描述信息")
public class UploadController {

    @Autowired
    //UploadService是我们在huanf-framework写的接口
    private OssUploadService ossUploadService;

    @PostMapping("/upload")
    @mySystemlog(xxbusinessName = "图片上传到七牛云")//接口描述，用于'日志记录'功能
    //MultipartFile是spring提供的接口，ResponseResult是我们在huanf-framework写的实体类
    public ResponseResult uploadImg(MultipartFile img){
        //图片上传到七牛云
        return ossUploadService.uploadImg(img);
    }
}
