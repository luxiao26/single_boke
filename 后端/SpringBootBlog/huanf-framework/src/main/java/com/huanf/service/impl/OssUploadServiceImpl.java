package com.huanf.service.impl;

import com.google.gson.Gson;
import com.huanf.domain.ResponseResult;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.exception.SystemException;
import com.huanf.service.OssUploadService;
import com.huanf.utils.PathUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author 35238
 * @date 2023/7/29 0029 11:41
 */
@Service
@Data//为成员变量生成get和set方法
@ConfigurationProperties(prefix = "myoss") //把OSSTest测试类的这一行注释掉，不然myoss被两个类读取会报错
//把文件上传到七牛云
public class OssUploadServiceImpl implements OssUploadService {

    @Override
    //MultipartFile是spring提供的接口
    public ResponseResult uploadImg(MultipartFile img) {

        //获取原始文件名
        String originalFilename = img.getOriginalFilename();

        // 获取文件大小
        long fileSize = img.getSize();

        // 判断文件大小是否超过2MB（2MB=2*1024*1024 bytes）
        if (fileSize > 2 * 1024 * 1024) {
            // 抛出文件大小超过限制的异常
            throw new SystemException(AppHttpCodeEnum.FILE_SIZE_ERROR);
        }

        //对原始文件名进行判断大小。只能上传png或jpg文件
        if(!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")){
            //AppHttpCodeEnum是我们在huanf-framework写的枚举类，FILE_TYPE_ERROR代表文件类型错误的提示
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }

        //如果满足判断条件，则上传文件到七牛云OSS，并得到一个图片外链访问地址。PathUtil是我们在huanf-framework工程写的工具类
        //PathUtils.generateFilePath(originalFilename)表示把原始文件名转换成指定文件名
        String filePath = PathUtils.generateFilePath(originalFilename);

        //下面用于调用的uploadOss方法返回的必须是String类型
        String url = uploadOss(img,filePath);
        //把得到的外链地址返回给前端
        return ResponseResult.okResult(url);
    }

    //----------------------------------上传文件到七牛云----------------------------------------

    //注意要从application.yml读取属性数据，下面的3个成员变量的名字必须对应application.yml的myoss属性的三个子属性名字
    private String xxaccessKey;
    private String xxsecretKey;
    private String xxbucket;

    //上传文件的具体代码。MultipartFile是spring提供的接口，作用是实现文件上传
    private String uploadOss(MultipartFile imgFile, String filePath){
        //构造一个带指定 Region 对象的配置类。你的七牛云OSS创建的是哪个区域的，那么就调用Region的什么方法即可
        Configuration cfg = new Configuration(Region.huanan());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        UploadManager uploadManager = new UploadManager(cfg);

        //打开七牛云，把鼠标悬浮在右上角的个人头像，然后就会看到'密钥管理'，点击进入就有你的密钥，把其中的AK和SK复制到下面两行
        //String accessKey = "_ibGP9wytjLCAZPqcFaWQNxbw7fMUvofSOvOFFR3";
        //String secretKey = "QSOAU-cv3sSDGNfVNPF6iXz-PsP5X9QTrjFI9zYw";
        //String bucket = "huanf-blog";
        //为避免上面3行暴露信息，我们会把信息写到application.yml里面，然后添加ConfigurationProperties注解、3个成员变量即可读取

        //文件名，如果写成null的话，就以文件内容的hash值作为文件名
        String key = filePath;

        try {

            //byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            //ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);

            //上面两行是官方写的(注释掉)，下面那几行是我们写的
            //把前端传过来的文件转换成InputStream对象
            InputStream xxinputStream = imgFile.getInputStream();

            Auth auth = Auth.create(xxaccessKey, xxsecretKey);
            String upToken = auth.uploadToken(xxbucket);

            try {
                //把前端传过来的xxinputStream图片上传到七牛云
                Response response = uploadManager.put(xxinputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println("上传成功! 生成的key是: "+putRet.key);
                System.out.println("上传成功! 生成的hash是: "+putRet.hash);
                return "https://www.huanf.top//"+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        }catch (Exception e) {
            //ignore
        }
        return "上传失败";
    }
}