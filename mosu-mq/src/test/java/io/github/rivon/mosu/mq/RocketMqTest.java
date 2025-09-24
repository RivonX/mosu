package io.github.rivon.mosu.mq;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试RocketMQ服务
 */
@Slf4j
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RocketMqTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSendMessageToRabbitmq() throws JsonProcessingException {
        // 构造订单消息
        Map<String, Object> order1 = new HashMap<>();
        order1.put("orderNO", "1111111111");
        order1.put("orderName", "测试订单");
        order1.put("orderPrice", 100.00);
        order1.put("orderTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 将订单数据转换为 JSON 字符串
        String payload = objectMapper.writeValueAsString(order1);

        // 发送消息
        String orderNo = (String) order1.get("orderNO"); // 获取订单号
        rocketMQTemplate.syncSendOrderly("ORDER_ADD", MessageBuilder.withPayload(payload).build(), orderNo);

        // 验证消息发送是否成功（需要根据您的实现，可能是通过监听器或者返回值进行验证）
        // 例如：
        // assertNotNull(rocketService.getLastSentMessage()); // 假设有方法获取最后发送的消息
    }
}
