package io.github.rivon.mosu.cache.service;


import lombok.Data;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redission服务
 *
 * @author allen
 */
@Data
public class RedissionService {

    private RedissonClient redissonClient;
    private static final long DEFAULT_GETLOCK_TIMEOUT = 5; // 默认获取锁的超时时间, 单位秒


    /**
     * 设置 String 类型缓存
     * @param key 缓存key
     * @param value 缓存值
     */
    public void setString(String key, String value) {
        redissonClient.getBucket(key).set(value);
    }

    /**
     * 获取 String 类型缓存
     * @param key 缓存key
     * @return 缓存值
     */
    public String getString(String key) {
        return (String) redissonClient.getBucket(key).get();
    }

    /**
     * 设置对象类型缓存
     * @param key 缓存key
     * @param value 缓存值
     * @param <T> 缓存值的类型
     */
    public <T> void setObject(String key, T value) {
        redissonClient.getBucket(key).set(value);
    }

    /**
     * 获取对象类型缓存
     * @param key 缓存key
     * @param <T> 缓存值的类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key) {
        return (T) redissonClient.getBucket(key).get();
    }

    /**
     * 设置 List 类型缓存
     * @param key 缓存key
     * @param list 缓存值
     * @param <T> 缓存值的类型
     */
    public <T> void setList(String key, List<T> list) {
        redissonClient.getList(key).addAll(list);
    }

    /**
     * 获取 List 类型缓存
     * @param key 缓存key
     * @return 缓存值
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        List<Object> rawList = redissonClient.getList(key).readAll();
        return rawList.stream()
                .map(clazz::cast)  // 显式转换每个元素
                .collect(Collectors.toList());
    }

    /**
     * 设置 Set 类型缓存
     * @param key 缓存key
     * @param set 缓存值
     * @param <T> 缓存值的类型
     */
    public <T> void setSet(String key, Set<T> set) {
        redissonClient.getSet(key).addAll(set);
    }

    /**
     * 获取 Set 类型缓存
     * @param key 缓存key
     * @return 缓存值
     */
    public <T> Set<T> getSet(String key) {
        return redissonClient.getSet(key);
    }

    /**
     * 设置 Map 类型缓存
     * @param key 缓存key
     * @param map 缓存值
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     */
    public <K, V> void setMap(String key, Map<K, V> map) {
        redissonClient.getMap(key).putAll(map);
    }

    /**
     * 获取 Map 类型缓存
     * @param key 缓存key
     * @param keyClass 缓存键的类型
     * @param valueClass 缓存值的类型
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return 缓存值
     */
    public <K, V> Map<K, V> getMap(String key, Class<K> keyClass, Class<V> valueClass) {
        Map<Object, Object> rawMap = redissonClient.getMap(key).readAllMap();
        Map<K, V> resultMap = new HashMap<>();

        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            // 使用类型检查并转换
            K keyConverted = keyClass.cast(entry.getKey());
            V valueConverted = valueClass.cast(entry.getValue());
            resultMap.put(keyConverted, valueConverted);
        }

        return resultMap;
    }

    /**
     * 设置 JSON 类型缓存
     *
     * @param key 缓存的键
     * @param value 缓存的值
     * @param <T> 缓存值的类型
     */
    public <T> void setJson(String key, T value) {
        redissonClient.getBucket(key).set(value);
    }

    /**
     * 获取 JSON 类型缓存
     *
     * @param key 缓存的键
     * @param clazz 缓存值的类型
     * @param <T> 缓存值的类型
     * @return 缓存的值
     */
    public <T> T getJson(String key, Class<T> clazz) {
        return clazz.cast(redissonClient.getBucket(key).get());
    }

    /**
     * 设置 JSON 数组类型缓存
     *
     * @param key 缓存的键
     * @param list 缓存的值
     * @param <T> 缓存值的类型
     */
    public <T> void setJsonArray(String key, List<T> list) {
        redissonClient.getList(key).addAll(list);
    }

    /**
     * 获取 JSON 数组类型缓存
     *
     * @param key 缓存的键
     * @param clazz 缓存值的类型
     * @param <T> 缓存值的类型
     * @return 缓存的值
     */
    public <T> List<T> getJsonArray(String key, Class<T> clazz) {
        List<Object> rawList = redissonClient.getList(key).readAll();
        return rawList.stream()
                .map(clazz::cast)  // 显式转换每个元素
                .collect(Collectors.toList());
    }

    /**
     * 设置缓存并设置过期时间
     *
     * @param key        缓存key
     * @param value      缓存值
     * @param expireTime 过期时间, 单位秒
     */
    public <T> void setWithExpire(String key, T value, long expireTime) {
        redissonClient.getBucket(key).set(value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存并判断是否存在
     * @param key    缓存key
     * @return       缓存值
     * @param <T>    缓存值的类型
     */
    public <T> T getAndCheck(String key) {
        T value = getObject(key);
        if (value == null) {
            throw new RuntimeException("Cache not found for key: " + key);
        }
        return value;
    }

    /**
     * 删除指定的 String 类型缓存
     * @param key 缓存key
     */
    public void deleteString(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 删除指定的 Object 类型缓存
     * @param key 缓存key
     */
    public void deleteObject(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 删除指定的 List 类型缓存
     * @param key 缓存key
     */
    public void deleteList(String key) {
        redissonClient.getList(key).delete();
    }

    /**
     * 删除指定的 Set 类型缓存
     * @param key 缓存key
     */
    public void deleteSet(String key) {
        redissonClient.getSet(key).delete();
    }

    /**
     * 删除指定的 Map 类型缓存
     * @param key 缓存key
     */
    public void deleteMap(String key) {
        redissonClient.getMap(key).delete();
    }

    /**
     * 删除指定的缓存
     * @param key 缓存key
     */
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 获取锁
     * @param lockKey 锁的key
     * @return true:获取成功, false:获取失败
     */
    public boolean tryLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(DEFAULT_GETLOCK_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 获取锁
     * @param lockKey 锁的key
     * @param waitTime 等待时间
     * @param leaseTime 锁的有效时间
     * @return true:获取成功, false:获取失败
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

}
