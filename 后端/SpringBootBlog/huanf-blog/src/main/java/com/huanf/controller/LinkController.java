package com.huanf.controller;

import com.huanf.annotation.mySystemlog;
import com.huanf.domain.Link;
import com.huanf.domain.ResponseResult;
import com.huanf.service.LinkService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 35238
 * @date 2023/7/22 0022 14:34
 */
@RestController
@RequestMapping("/link")
@Api(tags = "友链的相关接口文档", description = "我是描述信息")
public class LinkController {

    @Autowired
    //LinkService是我们在huanf-framework工程写的接口
    private LinkService linkService;


    @GetMapping("/getAllLink")
    @mySystemlog(xxbusinessName = "查询友链")//接口描述，用于'日志记录'功能
    //ResponseResult是我们在huanf-framework工程写的实体类
    public ResponseResult getAllLink(){
        return linkService.getAllLink();
    }

    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody Link link,HttpServletRequest request){


        if(link.getName()==null || link.getName().trim().isEmpty()) {
            return ResponseResult.errorResult(199,"名称不能为空");
        }
        if(link.getDescription()==null || link.getDescription().trim().isEmpty()) {
            return ResponseResult.errorResult(199,"描述不能为空");
        }
        if(link.getLogo()==null || link.getLogo().trim().isEmpty()) {
            return ResponseResult.errorResult(199,"logo不能为空");
        }
        if(link.getAddress()==null || link.getAddress().trim().isEmpty()) {
            return ResponseResult.errorResult(199,"地址不能为空");
        }

        List<Link> list = linkService.list();
        String lastIp = getIp(request);
        if (lastIp==null) {
            return ResponseResult.errorResult(199,"请勿使用代理，正直一点，别怂");
        }
        int count = 0;
        for (Link one : list) {
            String name = one.getName();
            if (name.contains("##")) {
                String[] parts = name.split("##");
                if (parts.length == 2) {
                    String ipPart = parts[1];
                    if (ipPart.equals(lastIp)) {
                        count++;
                        if (count == 3) {
                            return ResponseResult.errorResult(199,"一次最多提交3个，审核之后才可提交更多");
                        }
                    }
                }
            }
        }

        link.setCreateTime(new Date());
        link.setDelFlag(0);
        String name = link.getName();
        String nameAndIp = name + "-管理员审核之后必须点修改然后删掉所有短杠(包括短杠)后面的-##" + getIp(request);
        link.setName(nameAndIp);
        linkService.save(link);
        return ResponseResult.okResult();
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

}
