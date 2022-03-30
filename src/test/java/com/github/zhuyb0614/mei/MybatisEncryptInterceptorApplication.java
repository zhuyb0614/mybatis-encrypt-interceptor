package com.github.zhuyb0614.mei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.github.zhuyb0614.mei.mapper")
public class MybatisEncryptInterceptorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisEncryptInterceptorApplication.class, args);
    }

}