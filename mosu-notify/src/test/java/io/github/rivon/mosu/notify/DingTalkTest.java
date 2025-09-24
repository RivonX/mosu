package io.github.rivon.mosu.notify;


import io.github.rivon.mosu.notify.sender.DingTalkSender;
import io.github.rivon.mosu.notify.service.NotifyService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 钉钉发送服务
 * <p>
 * 注意NotifyService采用异步线程操作
 * 因此测试的时候需要睡眠一会儿，保证任务执行
 * <p>
 * 开发者需要确保：
 * 已经钉钉获取到webhook
 */
@Slf4j
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DingTalkTest {

    @Resource
    private NotifyService notifyService;

    @Resource
    private DingTalkSender.Message.At at;

    @Test
    public void text() {
        DingTalkSender dingTalkSender = new DingTalkSender();
        DingTalkSender.TextMessage textMessage = dingTalkSender.new TextMessage("测试内容", at);
        notifyService.notifyDingTalk(textMessage);
    }



    @Test
    public void link() {
        DingTalkSender dingTalkSender = new DingTalkSender();
        DingTalkSender.LinkMessage linkMessage = dingTalkSender.new LinkMessage(new DingTalkSender.LinkMessage.Link("标题", "测试 内容", "https://open.dingtalk.com/document/robots/custom-robot-access"));
        notifyService.notifyDingTalk(linkMessage);
    }

    @Test
    public void markdown() {
        DingTalkSender dingTalkSender = new DingTalkSender();
        notifyService.notifyDingTalk(dingTalkSender.new MarkdownMessage(new DingTalkSender.MarkdownMessage.Markdown("杭州天气1", " ### 测试1 \n #### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n")));
        notifyService.notifyDingTalk(dingTalkSender.new MarkdownMessage(new DingTalkSender.MarkdownMessage.Markdown("杭州天气1", " ### 测试1 \n #### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n")));
        notifyService.notifyDingTalk(dingTalkSender.new MarkdownMessage(new DingTalkSender.MarkdownMessage.Markdown("杭州天气1", " ### 测试1 \n #### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n")));
        notifyService.notifyDingTalk(dingTalkSender.new MarkdownMessage(new DingTalkSender.MarkdownMessage.Markdown("杭州天气1", " ### 测试1 \n #### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n")));
        notifyService.notifyDingTalk(dingTalkSender.new MarkdownMessage(new DingTalkSender.MarkdownMessage.Markdown("杭州天气1", " ### 测试1 \n #### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n")));
    }

}
