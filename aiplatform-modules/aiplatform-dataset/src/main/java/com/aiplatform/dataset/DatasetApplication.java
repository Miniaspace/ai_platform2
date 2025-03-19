package com.aiplatform.dataset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.aiplatform.dataset",
    "com.aiplatform.minio"
})
public class DatasetApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatasetApplication.class, args);
    }
} 