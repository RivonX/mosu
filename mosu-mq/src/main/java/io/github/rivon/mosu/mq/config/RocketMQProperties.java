package io.github.rivon.mosu.mq.config;

import lombok.Data;
import org.apache.rocketmq.common.topic.TopicValidator;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQ配置
 *
 * @author allen
 */
@Data
@ConfigurationProperties(prefix = "mosu.mq.rocket")
public class RocketMQProperties {

    /**
     * 是否启用RocketMQ.
     */
    private boolean enable;

    /**
     * 名称服务器地址.
     */
    private String nameServer;

    /**
     * 生产者配置
     */
    private Producer producer;


    @Data
    public static class Producer {

        /**
         * 生产者组
         */
        private String group;

        /**
         * 生产者组名称
         */
        private String groupName;

        /**
         * 生产者实例名称
         */
        private String namesrvAddr;

        /**
         * 超时毫秒数.
         * 默认3秒
         */
        private int sendMsgTimeout = 3000;

        /**
         * 消息体压缩阈值.
         * 对超过阈值的消息进行压缩，默认4K Byte
         */
        private int compressMsgBodyOverHowmuch = 1024 * 4;

        /**
         * 同步发送失败重试次数.
         * 默认为2，表示发送失败后最多再重试2次，总共最多发送3次。
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * 异步发送重试次数.
         * 暂时未用到异步发送
         */
        private int retryTimesWhenSendAsyncFailed = 2;

        /**
         * 当发送到broker后，broker存储失败是否重试其他broker.
         */
        private boolean retryAnotherBrokerWhenNotStoreOk = true;

        /**
         * 是否开启消息轨迹
         */
        private boolean enableMsgTrace = false;

        /**
         * 消息轨迹主题名称.
         * 默认为RMQ_SYS_TRACE_TOPIC
         * 系统自动生成的消息轨迹主题名称，如果不配置，则使用默认的消息轨迹主题名称。
         */
        private String customizedTraceTopic = TopicValidator.RMQ_SYS_TRACE_TOPIC;

        /**
         * 消息体最大2M.
         */
        private int maxMessageSize = 1024 * 1024 * 2;

    }
}