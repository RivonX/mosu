package io.github.rivon.mosu.notify.sender;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * 钉钉发送
 *
 * @author allen
 */
@Slf4j
@Data
public class DingTalkSender {

    private String webhook;
    private String secret;
    private RestTemplate restTemplate;

    public DingTalkSender() {
        restTemplate =  new RestTemplateBuilder().build();
    }

    public void send(Message message) {
        if (StringUtils.isBlank(webhook)) {
            throw new IllegalArgumentException("WebHook is null");
        }

        try {
            Date date = new Date();
            log.debug("开始发送消息：{}", date);
            String sign = sign(date.getTime());
            ResponseEntity<String> entity;
            if (sign == null) {
                entity = restTemplate.postForEntity(webhook, message, String.class);
            } else {
                entity = restTemplate.postForEntity(webhook + "&timestamp=" + date.getTime() + "&sign=" + sign, message, String.class);
            }

            log.debug("<===== success code {} body {}", entity.getStatusCodeValue(), entity.getBody());
        } catch (Exception e) {
            log.error("钉钉发送失败：", e);
        }
    }

    @SneakyThrows
    public String sign(Long timestamp) {
        if (StringUtils.isBlank(secret)) {
            return null;
        }
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);
    }

    @Data
    public abstract class Message implements Serializable {


        public abstract String getMsgtype();

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class At {
            private List<String> atMobiles;

            private Boolean isAtAll = false;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class TextMessage extends Message {
        public String getMsgtype() {
            return "text";
        }

        private Text text;

        private At at;

        public TextMessage(String text, At at) {
            this.text = new Text(text);
            this.at = at;
        }


        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Text implements Serializable {
            private String content;
        }
    }


    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class LinkMessage extends Message {
        public String getMsgtype() {
            return "link";
        }

        private Link link;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Link implements Serializable {
            private String title;
            private String text;
            private String messageUrl;
            private String picUrl;


            public Link(String title, String text, String messageUrl) {
                this.title = title;
                this.text = text;
                this.messageUrl = messageUrl;
            }
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class MarkdownMessage extends Message {
        public String getMsgtype() {
            return "markdown";
        }

        private Markdown markdown;


        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Markdown implements Serializable {
            private String title;
            private String text;
        }
    }

}