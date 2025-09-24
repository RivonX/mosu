package io.github.rivon.mosu.cache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 本地缓存服务类
 * caffine有三种缓存类型：Cache，LoadingCache，AsyncLoadingCache
 * 同时结合spring支持注解形式实现本地缓存, @Cacheable、 @CacheEvict、 @CachePut
 */
@Data
public class LocalCacheService {

    private Cache<String, Object> cache; // 缓存实例
    private int maximumSize; // 缓存最大容量
    private long expireAfterWrite; // 写入缓存后多久过期
    private long expireAfterAccess; // 访问缓存后多久过期
    private TimeUnit timeUnit; // 时间单位

    /**
     * 初始化本地缓存
     * 仅当启用缓存时才会调用此方法
     */
    public void initCache() {
        if (this.cache == null) {
            this.cache = Caffeine.newBuilder()
                    .maximumSize(this.maximumSize) // 设置缓存的最大容量，超过这个容量会根据策略移除缓存项
                    .expireAfterWrite(this.expireAfterWrite, this.timeUnit) // 设置写入缓存后多久过期，过期时间为expireAfterWrite指定的时间
                    .expireAfterAccess(this.expireAfterAccess, this.timeUnit) // 设置访问缓存后多久过期，过期时间为expireAfterAccess指定的时间
                    .build();
        }
    }

    /**
     * 获取缓存
     * @param key 缓存key
     * @return 缓存值
     */
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * 放入缓存
     * @param key 缓存key
     * @param value 缓存值
     */
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 缓存模板方法
     * @param key 缓存key
     * @param function 缓存值生成函数
     * @param <V> 缓存值类型
     * @return 缓存值
     */
    public <V> V put(String key, Function<? super String, ? extends V> function) {
        return (V) cache.get(key, function);
    }

    /**
     * 获取缓存值，并转换类型
     * @param key 缓存key
     * @param vClass 缓存值类型
     * @param <V> 缓存值类型
     * @return 缓存值
     */
    public <V> V getIfPresent(String key, Class<V> vClass) {
        return (V) cache.getIfPresent(key) ;
    }

    /**
     * 移除缓存
     * @param key 缓存key
     */
    public void invalidate(String key) {
        cache.invalidate(key); ;
    }

    /**
     * 移除所有缓存
     */
    public void invalidateAll() {
        cache.invalidateAll(); ;
    }

    /**
     * 获取所有缓存
     * @return 所有缓存
     */
    public Map<String, Object> getAll() {
        return cache.asMap();
    }

    /**
     * 批量放入缓存
     * @param map 缓存map
     */
    public void putAll(Map<String, Object> map) {
        cache.putAll(map);
    }

}
