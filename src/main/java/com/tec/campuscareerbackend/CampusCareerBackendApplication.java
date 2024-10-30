package com.tec.campuscareerbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tec.campuscareerbackend.mapper")
public class CampusCareerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusCareerBackendApplication.class, args);
    }

}
