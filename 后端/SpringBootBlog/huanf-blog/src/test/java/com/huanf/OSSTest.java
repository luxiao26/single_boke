package com.huanf;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author 35238
 * @date 2023/7/28 0028 14:44
 */
@Component
@SpringBootTest(classes = HuanfBlogApplication.class)
//@ConfigurationProperties(prefix = "myoss")//指定读取application.yml文件的myoss属性的数据
public class OSSTest {

    //注意要从application.yml读取属性数据，下面的3个成员变量的名字必须对应application.yml的myoss属性的三个子属性名字
    private String xxaccessKey;
    private String xxsecretKey;
    private String xxbucket;
    public void setXxaccessKey(String xxaccessKey) {
        this.xxaccessKey = xxaccessKey;
    }
    public void setXxsecretKey(String xxsecretKey) {
        this.xxsecretKey = xxsecretKey;
    }
    public void setXxbucket(String xxbucket) {
        this.xxbucket = xxbucket;
    }

    @Test
    public void testOss(){
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
        String key = "2023/7/my01.jpg";

        try {

            //byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            //ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);

            //上面两行是官方写的(注释掉)，下面那几行是我们写的
            InputStream xxinputStream = new FileInputStream("D:\\MyUploadFile\\myhead.jpg");

            Auth auth = Auth.create(xxaccessKey, xxsecretKey);
            String upToken = auth.uploadToken(xxbucket);

            try {
                Response response = uploadManager.put(xxinputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println("上传成功! 生成的key是: "+putRet.key);
                System.out.println("上传成功! 生成的hash是: "+putRet.hash);
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
    }
}
