package com.watson;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAspectJAutoProxy
@SpringBootApplication
@ServletComponentScan
@MapperScan({"com.watson.mapper","com.watson.dto"})
@EnableTransactionManagement
public class NyaTakeOutAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(NyaTakeOutAppApplication.class, args);
    }
}
