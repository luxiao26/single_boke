//package com.huanf.cronjob;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import java.time.LocalTime;
//
///**
// * @author 35238
// * @date 2023/7/31 0031 15:02
// */
//@Component//当前这个类要作为bean，且注入容器，否则不会生效
////定时任务，是一个快速入门的测试类，不再项目中实际使用
//public class myJob {
//
//    //
//    @Scheduled(cron = "0/5 * * * * ?")//在哪个方法添加了@Scheduled注解，哪个方法就会定时去执行
//    //上面那行@Scheduled注解的cron属性就是具体的定时规则。从每一分钟的0秒开始，每隔5秒钟就会执行下面那行的xxJob()方法
//    public void xxJob(){
//        //要定时执行的代码
//        System.out.println("定时任务执行了，现在的时间是: "+ LocalTime.now());
//    }
//
//}
