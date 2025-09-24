package io.github.rivon.mosu.mq.config;

import io.github.rivon.mosu.mq.enums.RabbitExchangeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * RabbitMQ配置
 *
 * @author allen
 */
@Data
@ConfigurationProperties(prefix = "mosu.mq.rabbit")
public class RabbitMQProperties {

    /**
     * 是否启用RabbitMQ.
     */
    private boolean enable;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 虚拟主机
     */
    private String virtualHost;

    /**
     * 连接超时时间
     */
    private int connectionTimeout;

    private boolean publisherReturns;

    private List<RabbitModuleInfo> modules;

    @Data
    public static class RabbitModuleInfo {

        /**
         * 路由Key
         */
        private String routingKey;
        /**
         * 队列信息
         */
        private Queue queue;
        /**
         * 交换机信息
         */
        private Exchange exchange;

        /**
         * 交换机信息类
         */
        @Data
        public static class Exchange {
            /**
             * 交换机类型
             * 默认直连交换机
             */
            private RabbitExchangeEnum type = RabbitExchangeEnum.DIRECT;
            /**
             * 交换机名称
             */
            private String name;
            /**
             * 是否持久化
             * 默认true持久化，重启消息不会丢失
             */
            private boolean durable = true;
            /**
             * 当所有队绑定列均不在使用时，是否自动删除交换机
             * 默认false，不自动删除
             */
            private boolean autoDelete = false;
            /**
             * 交换机其他参数
             */
            private Map<String, Object> arguments;
        }

        /**
         * 队列信息类
         */
        @Data
        public static class Queue {
            /**
             * 队列名称
             */
            private String name;
            /**
             * 是否持久化
             * 默认true持久化，重启消息不会丢失
             */
            private boolean durable = true;
            /**
             * 是否具有排他性
             * 默认false，可多个消费者消费同一个队列
             */
            private boolean exclusive = false;
            /**
             * 当消费者均断开连接，是否自动删除队列
             * 默认false,不自动删除，避免消费者断开队列丢弃消息
             */
            private boolean autoDelete = false;
            /**
             * 绑定死信队列的交换机名称
             */
            private String deadLetterExchange;
            /**
             * 绑定死信队列的路由key
             */
            private String deadLetterRoutingKey;


            private Map<String, Object> arguments;
        }
    }
}
