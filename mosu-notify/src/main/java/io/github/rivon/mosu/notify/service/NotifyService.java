package io.github.rivon.mosu.notify.service;


import io.github.rivon.mosu.notify.sender.DingTalkSender;
import io.github.rivon.mosu.notify.sender.SmsResult;
import io.github.rivon.mosu.notify.sender.SmsSender;
import lombok.Data;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通知服务
 *
 * @author allen
 */
@Data
public class NotifyService {
    /**
     * 邮件发送者
     */
    private String sendFrom;
    /**
     * 邮件接收者
     */
    private String sendTo;
    /**
     * 邮件发送接口
     */
    private MailSender mailSender;
    /**
     * 短信发送接口
     */
    private SmsSender smsSender;
    /**
     * 短信模板列表
     */
    private List<Map<String, String>> smsTemplate = new ArrayList<>();
    /**
     * 钉钉发送接口
     */
    private DingTalkSender dingTalkSender;

    /**
     * 是否启用短信
     *
     * @return 是否启用
     */
    public boolean isSmsEnable() {
        return smsSender != null;
    }

    /**
     * 是否启用邮件
     *
     * @return 是否启用
     */
    public boolean isMailEnable() {
        return mailSender != null;
    }

    /**
     * 是否启用钉钉
     *
     * @return 是否启用
     */
    public boolean isDingTalkEnable() {
        return dingTalkSender != null;
    }

    /**
     * 短信消息通知 异步
     *
     * @param phoneNumber 接收通知的电话号码
     * @param message     短信内容，这里短信内容必须在短信平台审核通过，阿里云必须使用模板
     */
    @Async
    public void notifySms(String phoneNumber, String message) {
        if (smsSender != null) {
            smsSender.send(phoneNumber, message);
        }
    }

    /**
     * 短信模板消息通知 异步
     *
     * @param phoneNumbers 必填，接收短信的手机号码
     *                     格式：
     *                     国内短信：11位手机号码，例如13812345678
     *                     国际/港澳台消息：国际区号+号码，例如85200000000
     *                     支持对多个手机号码发送短信，手机号码之间以英文逗号（,）分隔。
     *                     上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     *                     signName        必填，短信签名名称
     * @param notifyType   通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params       通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     *                     必填，短信模板变量对应的实际值，JSON格式
     *                     腾讯云短信模板参数是数组，因此短信模板形式如 “短信参数{1}， 短信参数{2}”
     *                     阿里云短信模板参数是JSON，因此短信模板形式如“短信参数{param1}， 短信参数{param2}”
     */
    @Async
    public void notifySmsTemplate(String phoneNumbers, NotifyType notifyType, String[] params) {
        if (smsSender == null) {
            return;
        }

        String templateIdStr = getTemplateId(notifyType, smsTemplate);
        if (templateIdStr == null) {
            return;
        }
        smsSender.sendWithTemplate(phoneNumbers, templateIdStr, params);
    }

    /**
     * 发送短信模版消息通知  同步
     *
     * @param phoneNumbers 必填，接收短信的手机号码。
     *                     格式：
     *                     国内短信：11位手机号码，例如15951955195。
     *                     国际/港澳台消息：国际区号+号码，例如85200000000。
     *                     支持对多个手机号码发送短信，手机号码之间以英文逗号（,）分隔。上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟
     *                     signName        必填，短信签名名称
     * @param notifyType   通知类别，通过该枚举值在配置文件中获取相应的模版ID
     *                     必填，短信模板变量对应的实际值，JSON格式
     *                     腾讯云短信模板参数是数组，因此短信模板形式如 “短信参数{1}， 短信参数{2}”
     *                     阿里云短信模板参数是JSON，因此短信模板形式如“短信参数{param1}， 短信参数{param2}”
     * @return SmsResult SmsResult
     */
    public SmsResult notifySmsTemplateSync(String phoneNumbers, NotifyType notifyType, String[] params) {
        if (smsSender == null) {
            return null;
        }
        return smsSender.sendWithTemplate(phoneNumbers, getTemplateId(notifyType, smsTemplate), params);
    }

    /**
     * 邮件消息通知, 异步
     * 接收者在spring.mail.sendto中指定
     *
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    @Async
    public void notifyMail(String subject, String content) {
        if (mailSender != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sendFrom);
            message.setTo(sendTo);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        }
    }

    /**
     * 获取短信发送模板 标识
     *
     * @param notifyType 模板类型 名称
     * @param values     模板列表 map
     * @return
     */
    private String getTemplateId(NotifyType notifyType, List<Map<String, String>> values) {
        for (Map<String, String> item : values) {
            String notifyTypeStr = notifyType.getType();
            if (item.get("name").equalsIgnoreCase(notifyTypeStr)) {
                return item.get("templateId");
            }
        }
        return null;
    }

    /**
     * 钉钉消息通知
     *
     * @param message     钉钉内容
     */
    @Async
    public void notifyDingTalk(DingTalkSender.Message message) {
        if (dingTalkSender != null) {
            dingTalkSender.send(message);
        }
    }
}
