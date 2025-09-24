package io.github.rivon.mosu.oss;


import com.amazonaws.services.s3.model.S3Object;
import io.github.rivon.mosu.oss.core.OssTemplate;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.InputStream;

/**
 * 这是一个用于测试 OSS 操作的单元测试类。
 * 通过 Spring Boot 测试框架来测试 OssTemplate 中的文件上传和下载功能。
 */
@WebAppConfiguration  // 指定这是一个 Web 应用测试配置
@ExtendWith(SpringExtension.class)  // 扩展 Spring 测试支持
@SpringBootTest  // 启动 Spring Boot 测试环境，自动加载 Spring 上下文
public class OssTest {

    @Resource
    private OssTemplate ossTemplate; // 注入 OssTemplate，用于与 OSS 服务交互

    @Test
    public void testOss() {
        try {
            // 加载类路径下的图片资源 "1.jpg"
            ClassPathResource classPathResource = new ClassPathResource("1.jpg");
            InputStream inputStream = classPathResource.getInputStream();

            // 上传图片到 OSS，上传到 "test" bucket 下，文件名为 "1.png"
            ossTemplate.putObject("test", "1.png", inputStream);

            // 从 OSS 下载图片，获取 "test" bucket 下的 "1.png" 文件
            S3Object feresources = ossTemplate.getObject("test", "1.png");
            System.out.println(feresources);  // 打印下载的图片对象
        } catch (Exception e) {
            e.printStackTrace(); // 捕获并打印异常
        }
    }
}
