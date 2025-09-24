package io.github.rivon.mosu.notify;


import io.github.rivon.mosu.notify.service.NotifyService;
import io.github.rivon.mosu.notify.service.NotifyType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 测试短信发送服务
 * <p>
 * 注意NotifyService采用异步线程操作
 * 因此测试的时候需要睡眠一会儿，保证任务执行
 * <p>
 * 开发者需要确保：
 * 1. 在腾讯云短信平台设置短信签名和短信模板notify.properties已经设置正确
 * 2. 在腾讯云短信平台设置短信签名和短信模板
 * 3. 在当前测试类设置好正确的手机号码
 */
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SmsTest {

    @Resource
    private NotifyService notifyService;

    @Test
    public void testCaptcha() {
        String phone = "13012345678";
        String[] params = new String[]{};
        notifyService.notifySmsTemplate(phone, NotifyType.REFUND, params);
    }
}
