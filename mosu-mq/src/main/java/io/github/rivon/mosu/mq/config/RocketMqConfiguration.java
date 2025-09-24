package io.github.rivon.mosu.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@ConditionalOnProperty(name = "mosu.mq.rocket.enable", havingValue = "true", matchIfMissing = false)
public class RocketMqConfiguration {

    static {
        System.setProperty("rocketmq.client.log.loadconfig", "false");
        System.setProperty("rocketmq.client.logUseSlf4j", "true");
    }

    private final RocketMQProperties rocketMQProperties;

    public RocketMqConfiguration(RocketMQProperties rocketMQProperties) {
        this.rocketMQProperties = rocketMQProperties;
    }

    /**
     * 创建默认的MQProducer
     *
     * @return MQProducer
     */
    @Bean("defaultMQProducer")
    public MQProducer mqProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(rocketMQProperties.getProducer().getGroup());
        producer.setNamesrvAddr(rocketMQProperties.getNameServer()); // 设置NameServer地址
        producer.setMaxMessageSize(producer.getMaxMessageSize()); // 设置最大消息大小
        producer.setSendMsgTimeout(producer.getSendMsgTimeout()); // 设置发送超时时间
        producer.setSendMsgTimeout(producer.getSendMsgTimeout()); // 设置发送超时时间
        producer.setRetryTimesWhenSendFailed(producer.getRetryTimesWhenSendFailed()); // 设置发送失败重试次数
        return producer;
    }

    @Bean
    @ConditionalOnMissingBean(name = "rocketMQTemplate")
    public RocketMQTemplate rocketMQTemplate(@Qualifier("defaultMQProducer") DefaultMQProducer defaultMQProducer) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(defaultMQProducer);
        return rocketMQTemplate;
    }

}
