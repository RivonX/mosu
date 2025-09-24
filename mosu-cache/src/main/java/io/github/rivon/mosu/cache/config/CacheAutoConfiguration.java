package io.github.rivon.mosu.cache.config;

import io.github.rivon.mosu.cache.service.LocalCacheAndRedissonService;
import io.github.rivon.mosu.cache.service.LocalCacheService;
import io.github.rivon.mosu.cache.service.RedissionService;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置bean
 *
 * @author allen
 **/
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({CacheProperties.class})
public class CacheAutoConfiguration {

    private final CacheProperties properties;

    /**
     * 配置 RedissonClient，用于 Redis 缓存操作
     *
     * @return RedissonClient 实例
     */
    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        String prefix = "redis://";

        // 判断是否启用 SSL
        if (properties.getRedission().isSsl()) {
            prefix = "rediss://";  // 加密模式
        }

        config.useSingleServer() // 使用单节点模式
                .setAddress(prefix + properties.getRedission().getHost() + ":" + properties.getRedission().getPort()) // 设置 Redis 连接地址，包含协议（redis 或 rediss）以及主机和端口
                .setConnectTimeout(properties.getRedission().getTimeOut()) // 设置连接超时时间，单位为毫秒
                .setPassword(properties.getRedission().getPassword()) // 设置 Redis 连接的密码
                .setDatabase(properties.getRedission().getDatabase()); // 设置 Redis 数据库索引，默认为 0

        // 设置值的序列化策略（使用 Jackson 序列化器）
        config.setCodec(new JsonJacksonCodec());

        return Redisson.create(config);
    }

    /**
     * 配置 RedissionService，用于 Redis 缓存服务
     *
     * @param redissonClient RedissonClient 实例
     * @return RedissionService 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RedissionService redissionService(RedissonClient redissonClient) {
        RedissionService redissionService = new RedissionService();

        CacheProperties.Redission redissionConfig = properties.getRedission();
        if (redissionConfig.isEnable()) {
            // 如果启用 Redis，设置 RedissonClient
            redissionService.setRedissonClient(redissonClient);
        }

        return redissionService;
    }

    /**
     * 配置 LocalCacheService，用于本地缓存服务
     *
     * @return LocalCacheService 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalCacheService localCacheService() {
        LocalCacheService localCacheService = new LocalCacheService();

        CacheProperties.LocalCache localCacheConfig = properties.getLocalCache();
        if (localCacheConfig.isEnable()) {
            localCacheService.setMaximumSize(localCacheConfig.getMaximumSize());
            localCacheService.setExpireAfterWrite(localCacheConfig.getExpireAfterWrite());
            localCacheService.setExpireAfterAccess(localCacheConfig.getExpireAfterAccess());
            localCacheService.setTimeUnit(TimeUnit.SECONDS);
            localCacheService.initCache(); // 启用缓存时初始化缓存
        }

        return localCacheService;
    }

    @Bean
    @ConditionalOnBean({RedissionService.class, LocalCacheService.class})
    @ConditionalOnMissingBean
    public LocalCacheAndRedissonService localCacheAndRedissonService(LocalCacheService localCacheService, RedissionService redissonService) {
        return new LocalCacheAndRedissonService(localCacheService, redissonService);
    }

    /**
     * 配置二级缓存，优先使用本地缓存，再使用 Redis 缓存
     *
     * @param redissonService  Redis 缓存服务
     * @param localCacheService 本地缓存服务
     * @return 缓存服务实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public Object cacheService(RedissionService redissonService, LocalCacheService localCacheService) {
        CacheProperties.LocalCache localCacheConfig = properties.getLocalCache();
        CacheProperties.Redission redissionConfig = properties.getRedission();

        // 根据配置优先选择缓存服务
        if (localCacheConfig.isEnable()) {
            if (redissionConfig.isEnable()) {
                // 二级缓存启用，优先使用本地缓存，再使用 Redis
                return new LocalCacheAndRedissonService(localCacheService, redissonService);
            }
            // 只有本地缓存启用
            return localCacheService;
        }

        // 只有 Redis 缓存启用
        if (redissionConfig.isEnable()) {
            return redissonService;
        }

        // 如果都未启用，返回 null
        return null;
    }
}
