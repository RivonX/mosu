package io.github.rivon.mosu.mq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Rabbit 生产者服务
 *
 * @author allen
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitProducerService {
    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param exchange     交换机名称
     * @param rabbitRouting 路由键
     * @param message      消息内容 Object类型
     */
    public void sendMessage(String exchange, String rabbitRouting, Object message) {
        this.rabbitTemplate.convertAndSend(exchange, rabbitRouting, message);
        log.info("向路由:{}, 发送消息成功:{}", rabbitRouting, message);
    }

    /**
     * 发送消息
     *
     * @param exchange     交换机名称
     * @param rabbitRouting 路由键
     * @param message      消息内容 Object类型
     * @param correlationData 关联数据
     */
    public void sendMessage(String exchange, String rabbitRouting, Object message, CorrelationData correlationData) {
        this.rabbitTemplate.convertAndSend(exchange, rabbitRouting, message, correlationData);
        log.info("向路由:{}, 发送消息成功:{}, correlationData:{}", new Object[]{rabbitRouting, message, correlationData});
    }

}
