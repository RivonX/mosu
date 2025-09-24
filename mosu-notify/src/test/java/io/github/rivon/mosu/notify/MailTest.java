package io.github.rivon.mosu.notify;


import io.github.rivon.mosu.notify.service.NotifyService;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 测试邮件发送服务
 * <p>
 * 注意NotifyService采用异步线程操作
 * 因此测试的时候需要睡眠一会儿，保证任务执行
 * <p>
 * 开发者需要确保：
 * 1. 在相应的邮件服务器设置正确notify.properties已经设置正确
 * 2. 在相应的邮件服务器设置正确
 */
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MailTest {

    @Resource
    private NotifyService notifyService;

    @Test
    public void testMail() {
        notifyService.notifyMail("订单信息", "订单111111已付款，请发货");
    }
}
