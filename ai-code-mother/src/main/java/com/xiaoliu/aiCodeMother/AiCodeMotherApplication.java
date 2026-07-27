package com.xiaoliu.aiCodeMother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
* 主程序入口
*
* @author xiaoliu
*/
@SpringBootApplication
@MapperScan("com.xiaoliu.aiCodeMother.mapper") //扫描mapper接口
@EnableCaching // 开启 Spring Cache 缓存
@EnableScheduling // 开启 Spring Scheduling 定时任务
public class AiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCodeMotherApplication.class, args);
        System.out.println("===================================");
        System.out.println("==       项目启动成功       ==");
        System.out.println("===================================");
    }

}
