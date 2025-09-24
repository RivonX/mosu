package io.github.rivon.mosu.notify.sender;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云短信服务
 *
 * @author allen
 */
@Slf4j
@Data
public class AliyunSmsSender implements SmsSender {

    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String sign;

    private final String okCode = "OK";
    private static String DOMAIN = "dysmsapi.aliyuncs.com";
    private static String VERSION = "2017-05-25";
    private static String SEND_SMS_ACTION = "SendSms";
    private static String REGIN_ID_NAME = "RegionId";
    private static String PHONE_NUMBERS = "PhoneNumbers";
    private static String SIGN_NAME = "SignName";
    private static String TEMPLATE_CODE = "TemplateCode";
    private static String TEMPLATE_PARAM = "TemplateParam";

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public SmsResult send(String phone, String content) {
        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }

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
    @Override
    public SmsResult sendWithTemplate(String phoneNumbers, String templateId, String[] params) {
        DefaultProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKeyId, this.accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(DOMAIN);
        request.setSysVersion(VERSION);
        request.setSysAction(SEND_SMS_ACTION);
        request.putQueryParameter(REGIN_ID_NAME, this.regionId);
        request.putQueryParameter(PHONE_NUMBERS, phoneNumbers);
        request.putQueryParameter(SIGN_NAME, this.sign);
        request.putQueryParameter(TEMPLATE_CODE, templateId);
        if (params.length > 0) {
            String templateParam = params[0];
            request.putQueryParameter(TEMPLATE_PARAM, templateParam);
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        try {
            CommonResponse response = client.getCommonResponse(request);
            smsResult.setResult(response);
            String responseData = response.getData();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseData);
            String code = jsonNode.get("code").asText();
            if (response.getHttpResponse().isSuccess() && okCode.equals(code)) {
                smsResult.setSuccessful(true);
            } else {
                log.error("短信发送失败：" + response.getData());
            }
        } catch (Exception e) {
            log.error("短信发送失败：", e);
        }

        return smsResult;
    }
}
