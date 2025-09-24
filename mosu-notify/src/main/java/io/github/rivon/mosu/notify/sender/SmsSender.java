package io.github.rivon.mosu.notify.sender;

/**
 * 短信发送
 *
 * @author allen
 */
public interface SmsSender {

    /**
     * 发送短信息
     * 阿里云必须要模板
     *
     * @param phone   接收通知的电话号码
     * @param content 短消息内容
     * @return SmsResult SmsResult
     */
    @Deprecated
    SmsResult send(String phone, String content);

    /**
     * 短信发送,支持向多个不同的手机号码发送同样内容的短信
     *
     * @param phoneNumbers 必填，接收短信的手机号码。
     *                     格式：
     *                     国内短信：11位手机号码，例如15951955195。
     *                     国际/港澳台消息：国际区号+号码，例如85200000000。
     *                     支持对多个手机号码发送短信，手机号码之间以英文逗号（,）分隔。上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟
     *                     signName        必填，短信签名名称
     * @param templateId   必填，短信模板ID
     * @param params       必填，短信模板变量对应的实际值，JSON格式
     *                     腾讯云短信模板参数是数组，因此短信模板形式如 “短信参数{1}， 短信参数{2}”
     *                     阿里云短信模板参数是JSON，因此短信模板形式如“短信参数{param1}， 短信参数{param2}”
     * @return SmsResult SmsResult
     */
    SmsResult sendWithTemplate(String phoneNumbers, String templateId, String[] params);
}
