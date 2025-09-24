package io.github.rivon.mosu.notify.config;


import io.github.rivon.mosu.notify.sender.AliyunSmsSender;
import io.github.rivon.mosu.notify.sender.DingTalkSender;
import io.github.rivon.mosu.notify.sender.TencentSmsSender;
import io.github.rivon.mosu.notify.service.NotifyService;
import com.github.qcloudsms.SmsSingleSender;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

/**
 * 通知配置bean
 *
 * @author allen
 **/
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(NotifyProperties.class)
public class NoticeAutoConfiguration {

    private final NotifyProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public NotifyService notifyService() {
        NotifyService notifyService = new NotifyService();

        NotifyProperties.Mail mailConfig = properties.getMail();
        if (mailConfig.isEnable()) {
            notifyService.setMailSender(mailSender());
            notifyService.setSendFrom(mailConfig.getSendfrom());
            notifyService.setSendTo(mailConfig.getSendto());
        }

        NotifyProperties.Sms smsConfig = properties.getSms();
        if (smsConfig.isEnable()) {
            if ("aliyun".equalsIgnoreCase(smsConfig.getActive())) {
                notifyService.setSmsSender(aliyunSmsSender());
            }
            if ("tencent".equalsIgnoreCase(smsConfig.getActive())) {
                notifyService.setSmsSender(tencentSmsSender());
            }
            notifyService.setSmsTemplate(smsConfig.getTemplate());
        }

        NotifyProperties.DingTalk dingTalkConfig = properties.getDingTalk();
        if (dingTalkConfig.isEnable()) {
            notifyService.setDingTalkSender(dingTalkSender());
        }

        return notifyService;
    }

    /**
     * 阿里云短信发送
     */
    public AliyunSmsSender aliyunSmsSender() {
        NotifyProperties.Sms smsConfig = properties.getSms();
        AliyunSmsSender smsSender = new AliyunSmsSender();
        NotifyProperties.Sms.Aliyun aliyun = smsConfig.getAliyun();
        smsSender.setSign(smsConfig.getSign());
        smsSender.setRegionId(aliyun.getRegionId());
        smsSender.setAccessKeyId(aliyun.getAccessKeyId());
        smsSender.setAccessKeySecret(aliyun.getAccessKeySecret());
        return smsSender;
    }

    /**
     * 腾讯短信发送
     */
    public TencentSmsSender tencentSmsSender() {
        NotifyProperties.Sms smsConfig = properties.getSms();
        TencentSmsSender smsSender = new TencentSmsSender();
        NotifyProperties.Sms.Tencent tencent = smsConfig.getTencent();
        smsSender.setSender(new SmsSingleSender(tencent.getAppId(), tencent.getAppKey()));
        smsSender.setSign(smsConfig.getSign());
        return smsSender;
    }

    /**
     * 邮件发送
     */
    public JavaMailSender mailSender() {
        NotifyProperties.Mail mailConfig = properties.getMail();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfig.getHost());
        mailSender.setUsername(mailConfig.getUsername());
        mailSender.setPassword(mailConfig.getPassword());
        mailSender.setPort(mailConfig.getPort());
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.timeout", 5000);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.socketFactory.fallback", "false");
        //阿里云 必须加入配置 outlook配置又不需要 视情况而定.发送不成功多数是这里的配置问题
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", mailConfig.getPort());
        properties.put("debug", true);
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

    /**
     * 钉钉发送
     */
    public DingTalkSender dingTalkSender() {
        NotifyProperties.DingTalk dingTalkConfig = properties.getDingTalk();
        DingTalkSender dingTalkSender = new DingTalkSender();
        dingTalkSender.setWebhook(dingTalkConfig.getWebhook());
        dingTalkSender.setSecret(dingTalkSender.getSecret());
        return dingTalkSender;
    }

    @Bean
    public DingTalkSender.Message.At messageAt() {
        return new DingTalkSender.Message.At();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
