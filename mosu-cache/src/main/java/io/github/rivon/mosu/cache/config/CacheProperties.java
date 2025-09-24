package io.github.rivon.mosu.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mosu.cache")
public class CacheProperties {

    private LocalCache localCache;
    private Redission redission;

    @Data
    public static class LocalCache {
        private boolean enable; // 是否启用 默认不启用 false
        private int maximumSize; // 最大容量 默认10000
        private long expireAfterWrite; // 写入后过期时间 默认1天 单位毫秒
        private long expireAfterAccess; // 最后一次访问后过期时间 默认30分钟 单位毫秒
    }

    @Data
    public static class Redission {
        private boolean enable; // 是否启用 默认不启用 false
        private String host;  // 连接地址
        private int port; // 连接端口号
        private int database; // 数据库索引 默认0
        private String password; // 连接密码
        private int timeOut; // 超时时间 单位毫秒 默认3000毫秒
        private boolean ssl; // 是否加密 默认不加密 false
    }
}

