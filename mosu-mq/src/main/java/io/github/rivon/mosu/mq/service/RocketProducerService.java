package io.github.rivon.mosu.mq.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;

/**
 * RocketMQ 服务
 *
 * @author allen
 */
@Slf4j
@RequiredArgsConstructor
public class RocketProducerService {
    private final RocketMQTemplate rocketMQTemplate;

    @PostConstruct
    public void init() {
        log.info("---RocketMq助手初始化---");
    }

    /**
     * 发送异步消息，使用默认的回调函数
     *
     * @param <T>      枚举类型
     * @param topic    消息的主题（通常是枚举类型的name()）
     * @param message  消息实体，包含消息内容和其他元数据
     */
    public <T extends Enum<T>> void asyncSend(T topic, Message<?> message) {
        asyncSend(topic.name(), message, getDefaultSendCallBack());
    }

    /**
     * 发送异步消息，使用自定义的回调函数
     *
     * @param <T>          枚举类型
     * @param topic        消息的主题（通常是枚举类型的name()）
     * @param message      消息实体，包含消息内容和其他元数据
     * @param sendCallback 消息发送后的回调函数，用于处理成功和失败的情况
     */
    public <T extends Enum<T>> void asyncSend(T topic, Message<?> message, SendCallback sendCallback) {
        asyncSend(topic.name(), message, sendCallback);
    }

    /**
     * 发送异步消息，使用默认回调函数
     *
     * @param topic   消息的主题（字符串类型）
     * @param message 消息实体，包含消息内容和其他元数据
     */
    public void asyncSend(String topic, Message<?> message) {
        rocketMQTemplate.asyncSend(topic, message, getDefaultSendCallBack());
    }

    /**
     * 发送异步消息，使用自定义回调函数
     *
     * @param topic        消息的主题（字符串类型）
     * @param message      消息实体，包含消息内容和其他元数据
     * @param sendCallback 消息发送后的回调函数，用于处理成功和失败的情况
     */
    public void asyncSend(String topic, Message<?> message, SendCallback sendCallback) {
        rocketMQTemplate.asyncSend(topic, message, sendCallback);
    }

    /**
     * 发送异步消息，使用自定义回调函数和超时时间
     *
     * @param topic        消息的主题（字符串类型）
     * @param message      消息实体，包含消息内容和其他元数据
     * @param sendCallback 消息发送后的回调函数
     * @param timeout      超时时间，单位毫秒
     */
    public void asyncSend(String topic, Message<?> message, SendCallback sendCallback, long timeout) {
        rocketMQTemplate.asyncSend(topic, message, sendCallback, timeout);
    }

    /**
     * 发送异步消息，使用自定义回调函数、超时时间和延迟级别
     *
     * @param topic        消息的主题（字符串类型）
     * @param message      消息实体，包含消息内容和其他元数据
     * @param sendCallback 消息发送后的回调函数
     * @param timeout      超时时间，单位毫秒
     * @param delayLevel   延迟级别，0 表示不延迟，1 表示延迟 1s，依此类推
     */
    public void asyncSend(String topic, Message<?> message, SendCallback sendCallback, long timeout, int delayLevel) {
        rocketMQTemplate.asyncSend(topic, message, sendCallback, timeout, delayLevel);
    }

    /**
     * 发送顺序消息，使用枚举类型的主题
     *
     * @param <T>      枚举类型
     * @param topic    消息的主题（枚举类型的name()）
     * @param message  消息实体，包含消息内容和其他元数据
     * @param hashKey  用于确保消息的顺序性（例如，根据某个字段分配）
     */
    public <T extends Enum<T>> void syncSendOrderly(T topic, Message<?> message, String hashKey) {
        syncSendOrderly(topic.name(), message, hashKey);
    }

    /**
     * 发送顺序消息，使用字符串类型的主题
     *
     * @param topic    消息的主题（字符串类型）
     * @param message  消息实体，包含消息内容和其他元数据
     * @param hashKey  用于确保消息的顺序性（例如，根据某个字段分配）
     */
    public void syncSendOrderly(String topic, Message<?> message, String hashKey) {
        log.info("发送顺序消息，topic:" + topic + ",hashKey:" + hashKey);
        rocketMQTemplate.syncSendOrderly(topic, message, hashKey);
    }

    /**
     * 发送顺序消息，使用超时设置
     *
     * @param topic    消息的主题（字符串类型）
     * @param message  消息实体，包含消息内容和其他元数据
     * @param hashKey  用于确保消息的顺序性
     * @param timeout  超时时间，单位毫秒
     */
    public void syncSendOrderly(String topic, Message<?> message, String hashKey, long timeout) {
        log.info("发送顺序消息，topic:" + topic + ",hashKey:" + hashKey + ",timeout:" + timeout);
        rocketMQTemplate.syncSendOrderly(topic, message, hashKey, timeout);
    }

    /**
     * 获取默认的消息发送回调函数
     *
     * @return SendCallback 默认回调函数，处理发送成功或失败的情况
     */
    private SendCallback getDefaultSendCallBack() {
        return new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("---发送MQ成功---");
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("---发送MQ失败---"+throwable.getMessage(), throwable.getMessage());
            }
        };
    }

    /**
     * 销毁RocketMQ助手，注销日志记录
     */
    @PreDestroy
    public void destroy() {
        log.info("---RocketMq助手注销---");
    }
}
