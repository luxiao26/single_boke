package com.huanf.service;

import com.huanf.domain.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 35238
 * @date 2023/7/29 0029 11:32
 */
public interface OssUploadService {
    //图片上传到七牛云
    ResponseResult uploadImg(MultipartFile img);
}
