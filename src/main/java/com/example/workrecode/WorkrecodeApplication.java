package com.example.workrecode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * 员工管理系统主启动类
 */
@SpringBootApplication
@MapperScan("com.example.workrecode.mapper") // 扫描Mapper接口
@CrossOrigin // 允许跨域请求
public class WorkrecodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkrecodeApplication.class, args);
    }

}