package io.github.rivon.mosu.notify.sender;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 腾讯云短信服务
 *
 * @author allen
 */
@Slf4j
@Data
public class TencentSmsSender implements SmsSender {

    private SmsSingleSender sender;
    private String sign;

    @Override
    public SmsResult send(String phone, String content) {
        try {
            SmsSingleSenderResult result = sender.send(0, "86", phone, content, "", "");
            log.debug(String.valueOf(result));

            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            log.error(e.getMessage(), e);
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }

    /**
     * @param phone      电话
     * @param templateId 模板
     * @param params     必填，短信模板变量对应的实际值，JSON格式
     *                   腾讯云短信模板参数是数组，因此短信模板形式如 “短信参数{1}， 短信参数{2}”
     *                   阿里云短信模板参数是JSON，因此短信模板形式如“短信参数{param1}， 短信参数{param2}”
     * @return
     */
    @Override
    public SmsResult sendWithTemplate(String phone, String templateId, String[] params) {
        try {
            SmsSingleSenderResult result = sender.sendWithParam("86", phone, Integer.parseInt(templateId), params, this.sign, "", "");
            log.debug(String.valueOf(result));

            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            log.error(e.getMessage(), e);
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }
}
