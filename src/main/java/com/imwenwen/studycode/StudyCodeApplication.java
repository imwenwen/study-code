package com.imwenwen.studycode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.imwenwen.studycode.dto")
public class StudyCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyCodeApplication.class, args);
    }

}
