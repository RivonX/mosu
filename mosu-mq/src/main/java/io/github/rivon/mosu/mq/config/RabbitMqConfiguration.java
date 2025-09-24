package io.github.rivon.mosu.mq.config;

import com.rabbitmq.client.Channel;
import io.github.rivon.mosu.mq.enums.RabbitExchangeEnum;
import io.github.rivon.mosu.mq.service.RabbitProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
@ConditionalOnProperty(name = "mosu.mq.rabbit.enable", havingValue = "true", matchIfMissing = false)
public class RabbitMqConfiguration {

    private final RabbitMQProperties rabbitMQProperties;
    private final ApplicationContext applicationContext;

    public RabbitMqConfiguration(RabbitMQProperties rabbitMQProperties, ApplicationContext applicationContext) {
        this.rabbitMQProperties = rabbitMQProperties;
        this.applicationContext = applicationContext;
    }
    /**
     * 配置 RabbitMQ 连接工厂
     */
    @Bean("rabbitConnectionFactory")
    @ConditionalOnMissingBean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMQProperties.getHost());
        connectionFactory.setPort(rabbitMQProperties.getPort());
        connectionFactory.setUsername(rabbitMQProperties.getUsername());
        connectionFactory.setPassword(rabbitMQProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitMQProperties.getVirtualHost());
        return connectionFactory;
    }

    /**
     * 创建 RabbitTemplate Bean，并注入 ConnectionFactory
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplate rabbitTemplate(@Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息成功确认，correlationData:{}", correlationData);
            } else {
                log.error("消息确认失败，correlationData:{}, cause:{}", correlationData, cause);
            }
        });

        rabbitTemplate.setReturnsCallback((returnedMessage) -> {
            Message message = returnedMessage.getMessage();  // 获取原始消息
            byte[] body = message.getBody();  // 获取消息体
            String content = new String(body, StandardCharsets.UTF_8);  // 将字节数组转为字符串

            log.error("消息发送失败，消息内容：{}，返回码：{}，返回文本：{}，交换机：{}，路由键：{}",
                    content, returnedMessage.getReplyCode(), returnedMessage.getReplyText(),
                    returnedMessage.getExchange(), returnedMessage.getRoutingKey());
        });

        return rabbitTemplate;
    }

    /**
     * 根据配置创建队列、交换机
     */
    public void declareRabbitModule(@Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory) {
        List<RabbitMQProperties.RabbitModuleInfo> rabbitModuleInfos = rabbitMQProperties.getModules();
        if (CollectionUtils.isEmpty(rabbitModuleInfos)) {
            return;
        }

        try (Channel channel = connectionFactory.createConnection().createChannel(false)) {
            for (RabbitMQProperties.RabbitModuleInfo rabbitModuleInfo : rabbitModuleInfos) {
                configParamValidate(rabbitModuleInfo);

                // 队列
                Queue queue = convertQueue(rabbitModuleInfo.getQueue());
                // 交换机
                Exchange exchange = convertExchange(rabbitModuleInfo.getExchange());

                // 创建队列
                if (!isExistQueue(channel, queue.getName())) {
                    channel.queueDeclare(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), queue.getArguments());
                }

                // 创建交换机
                channel.exchangeDeclare(exchange.getName(), exchange.getType(), exchange.isDurable(), exchange.isAutoDelete(), exchange.getArguments());

                // 绑定队列与交换机
                String routingKey = rabbitModuleInfo.getRoutingKey();
                channel.queueBind(queue.getName(), exchange.getName(), routingKey);
            }
        } catch (Exception e) {
            log.error("RabbitMQ初始化异常", e);
        }
    }

    /**
     * RabbitMQ动态配置参数校验
     *
     * @param rabbitModuleInfo 队列和交换机机绑定关系
     */
    public void configParamValidate(RabbitMQProperties.RabbitModuleInfo rabbitModuleInfo) {
        String routingKey = rabbitModuleInfo.getRoutingKey();
        Assert.isTrue(StringUtils.isNotBlank(routingKey), "RoutingKey 未配置");
        Assert.isTrue(rabbitModuleInfo.getExchange() != null, String.format("routingKey:%s未配置exchange", routingKey));
        Assert.isTrue(StringUtils.isNotBlank(rabbitModuleInfo.getExchange().getName()), String.format("routingKey:%s未配置exchange的name属性", routingKey));
        Assert.isTrue(rabbitModuleInfo.getQueue() != null, String.format("routingKey:%s未配置queue", routingKey));
        Assert.isTrue(StringUtils.isNotBlank(rabbitModuleInfo.getQueue().getName()), String.format("routingKey:%s未配置exchange的name属性", routingKey));
    }

    /**
     * 转换生成RabbitMQ队列
     *
     * @param queue 队列
     * @return Queue
     */
    public Queue convertQueue(RabbitMQProperties.RabbitModuleInfo.Queue queue) {
        Map<String, Object> arguments = queue.getArguments();

        // 转换ttl的类型为long
        if (arguments != null && arguments.containsKey("x-message-ttl")) {
            arguments.put("x-message-ttl", Long.parseLong(arguments.get("x-message-ttl").toString()));
        }

        // 是否需要绑定死信队列
        String deadLetterExchange = queue.getDeadLetterExchange();
        String deadLetterRoutingKey = queue.getDeadLetterRoutingKey();
        if (StringUtils.isNotBlank(deadLetterExchange) && StringUtils.isNotBlank(deadLetterRoutingKey)) {
            if (arguments == null) {
                arguments = new HashMap<>(4);
            }
            arguments.put("x-dead-letter-exchange", deadLetterExchange);
            arguments.put("x-dead-letter-routing-key", deadLetterRoutingKey);
        }
        return new Queue(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), arguments);
    }

    /**
     * 转换生成RabbitMQ交换机
     *
     * @param exchangeInfo 交换机信息
     * @return Exchange
     */
    public Exchange convertExchange(RabbitMQProperties.RabbitModuleInfo.Exchange exchangeInfo) {
        AbstractExchange exchange;
        RabbitExchangeEnum exchangeType = exchangeInfo.getType();
        String exchangeName = exchangeInfo.getName();
        boolean isDurable = exchangeInfo.isDurable();
        boolean isAutoDelete = exchangeInfo.isAutoDelete();

        Map<String, Object> arguments = exchangeInfo.getArguments();

        exchange = switch (exchangeType) {
            case DIRECT -> new DirectExchange(exchangeName, isDurable, isAutoDelete, arguments);
            case TOPIC -> new TopicExchange(exchangeName, isDurable, isAutoDelete, arguments);
            case FANOUT -> new FanoutExchange(exchangeName, isDurable, isAutoDelete, arguments);
            case HEADERS -> new HeadersExchange(exchangeName, isDurable, isAutoDelete, arguments);
        };
        return exchange;
    }

    /**
     * 判断队列是否存在
     *
     * @param channel   RabbitMQ Channel
     * @param queueName 队列名
     * @return boolean
     */
    private boolean isExistQueue(Channel channel, String queueName) {
        try {
            channel.queueDeclarePassive(queueName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 创建 RabbitProducerService
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitProducerService rabbitProducerService(RabbitTemplate rabbitTemplate) {
        return new RabbitProducerService(rabbitTemplate);
    }

    /**
     * 使用事件监听器来延迟声明 RabbitMQ 模块，避免循环依赖
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // 手动获取 connectionFactory
        ConnectionFactory connectionFactory = applicationContext.getBean("rabbitConnectionFactory", ConnectionFactory.class);
        if (connectionFactory != null) {
            log.info("初始化rabbitmq交换机、队列----------------start");
            declareRabbitModule(connectionFactory);
            log.info("初始化rabbitmq交换机、队列----------------end");
        } else {
            log.error("RabbitMQ ConnectionFactory 未初始化");
        }
    }
}
