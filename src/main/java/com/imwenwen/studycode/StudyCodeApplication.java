package com.imwenwen.studycode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.imwenwen.studycode.dto")
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class StudyCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyCodeApplication.class, args);
    }

}
