package io.github.rivon.mosu.mq;


import io.github.rivon.mosu.mq.service.RabbitProducerService;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 测试RabbitMQ服务
 */
@Slf4j
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RabbitMqTest {

    @Resource
    private RabbitProducerService rabbitProducerService;

    @Test
    public void testSendMessageToRabbitmq() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: non-existent-exchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitProducerService.sendMessage("mytest", "default.queue.key", JSON.toJSONString(map));

        // 等待一段时间，确保确认回调能够被触发
        try {
            Thread.sleep(1000);  // 等待 1 秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 消费消息并手动确认
     *
     * @param message 消息内容
     * @param channel RabbitMQ 通道，用于手动确认消息
     * @throws Exception 如果在消费过程中发生错误，抛出此异常
     */
    public void consumeMessageWithAck(Object message, Channel channel) throws Exception {
        try {
            log.info("接收到消息: {}", message);
            // 在这里进行消息处理

            // 手动确认消息
            if (channel != null) {
                channel.basicAck(1L, false);  // 注意：这里需要传入正确的 deliveryTag
            }
            log.info("消息确认成功: {}", message);
        } catch (Exception e) {
            // 消费失败时进行拒绝
            if (channel != null) {
                channel.basicReject(1L, false);
            }
            log.error("消费失败，拒绝消息: {}", message, e);
        }
    }

    /**
     * 测试消费消息并手动确认
     *
     * @throws Exception 如果在消费过程中发生错误，抛出此异常
     */
    @Test
    public void consumeMessageWithAck() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("custId", 211212L);
        String message = JSON.toJSONString(map);

        // 发送消息到 RabbitMQ
        rabbitProducerService.sendMessage("mytest", "default.queue.key", message);

        // 模拟接收消息并手动确认
        Channel mockChannel = null; // 这里模拟一个 Channel，可以为 null 或者使用 Mockito 模拟
        consumeMessageWithAck(message, mockChannel);  // 调用消费方法
    }
}
