package io.github.rivon.mosu.oss;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import io.github.rivon.mosu.oss.core.OssProperties;
import io.github.rivon.mosu.oss.core.OssTemplate;
import io.github.rivon.mosu.oss.core.OssTemplateImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss配置bean
 *
 * @author NightRowan
 **/
@Configuration // 声明该类为Spring配置类
@RequiredArgsConstructor // 使用Lombok自动生成带有所有final字段的构造函数
@EnableConfigurationProperties(OssProperties.class) // 启用OssProperties类的配置绑定
public class OssAutoConfiguration {

    /**
     * 定义一个Bean，返回AmazonS3客户端实例
     */
    @Bean
    @ConditionalOnMissingBean  // 如果容器中没有AmazonS3类型的Bean，才会创建此Bean
    public AmazonS3 ossClient(OssProperties ossProperties) {
        // 客户端配置，主要是全局的配置信息
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(ossProperties.getMaxConnections()); // 设置最大连接数，来自配置文件

        // url以及region配置，用于指定S3服务的URL和区域
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                ossProperties.getEndpoint(), ossProperties.getRegion());

        // 凭证配置，使用访问密钥和秘密密钥创建凭证对象
        AWSCredentials awsCredentials = new BasicAWSCredentials(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials); // 创建凭证提供者

        // 构建并返回AmazonS3客户端实例
        return AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration) // 配置S3服务的端点和区域
                .withClientConfiguration(clientConfiguration) // 配置客户端的全局配置
                .withCredentials(awsCredentialsProvider) // 配置凭证提供者
                .disableChunkedEncoding() // 禁用分块编码
                .withPathStyleAccessEnabled(ossProperties.getPathStyleAccess()) // 配置路径样式访问
                .build(); // 构建并返回AmazonS3客户端
    }

    /**
     *  定义一个Bean，返回OssTemplate实例，只有在AmazonS3已经存在的情况下才会创建此Bean
     */
    @Bean
    @ConditionalOnBean(AmazonS3.class) // 当容器中已经有AmazonS3 Bean时创建此Bean
    public OssTemplate ossTemplate(AmazonS3 amazonS3) {
        // 创建OssTemplate实例，依赖AmazonS3客户端来实现OSS操作
        return new OssTemplateImpl(amazonS3); // 返回实现了OssTemplate接口的实例
    }
}
