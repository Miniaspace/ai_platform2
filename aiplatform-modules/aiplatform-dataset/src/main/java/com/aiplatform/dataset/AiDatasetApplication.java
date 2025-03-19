package com.aiplatform.dataset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.aiplatform.common.security.annotation.EnableCustomConfig;
import com.aiplatform.common.security.annotation.EnableRyFeignClients;
import com.aiplatform.minio.config.MinioConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * 数据集管理模块
 * 
 * @author aiplatform
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
@EnableConfigurationProperties(MinioConfig.class)
@ComponentScan(basePackages = {
    "com.aiplatform.dataset",
    "com.aiplatform.minio"
})
@OpenAPIDefinition(
    info = @Info(
        title = "数据集管理模块 API",
        version = "1.0",
        description = "数据集管理模块的接口文档"
    )
)
public class AiDatasetApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AiDatasetApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  数据集管理模块启动成功   ლ(´ڡ`ლ)ﾞ");
    }
} 