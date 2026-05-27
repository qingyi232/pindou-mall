package com.pindou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pindou.mapper")
public class PindouApplication {
    public static void main(String[] args) {
        SpringApplication.run(PindouApplication.class, args);
    }
}
