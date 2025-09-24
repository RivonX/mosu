package io.github.rivon.mosu.notify.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通知配置
 *
 * @author allen
 */
@Data
@ConfigurationProperties(prefix = "mosu.notify")
public class NotifyProperties {

    private Sms sms;

    private Mail mail;

    private DingTalk dingTalk;

    @Data
    public static class Sms {
        private boolean enable;
        private String active;
        private String sign;
        private Aliyun aliyun;
        private Tencent tencent;
        private List<Map<String, String>> template = new ArrayList<>();

        @Data
        public static class Aliyun {
            private String regionId;
            private String accessKeyId;
            private String accessKeySecret;
        }

        @Data
        public static class Tencent {
            private int appId;
            private String appKey;
        }
    }

    @Data
    public static class Mail {
        private boolean enable;
        private String host;
        private String username;
        private String password;
        private String sendfrom;
        private String sendto;
        private Integer port;
    }

    @Slf4j
    @Data
    public static class DingTalk implements InitializingBean {
        private boolean enable;
        /**
         * 钉钉机器人的webhook
         */
        private String webhook;
        /**
         * 多个手机号用','分隔
         */
        private String atMobiles;

        /**
         * 多个关键词用','分隔
         */
        private String keywords;

        /**
         * 是否 @ 所有人
         */
        private Boolean atAll = false;

        /**
         * 配置签名 secret
         */
        private String secret;

        @Override
        public void afterPropertiesSet() {
            if (StringUtils.isBlank(webhook)) {
                log.error("未配置 机器人 webhook ，钉钉通知功能无法使用");
            }
        }
    }
}
