package io.github.rivon.mosu.cache.service;

/**
 * 本地缓存与 Redisson 缓存服务
 * <p>
 * 本类提供了一个服务，它结合了本地缓存与 Redis 缓存。首先会尝试从本地缓存中获取数据，如果本地缓存未命中，则会尝试从 Redis 中获取数据。如果从 Redis 获取成功，还会将数据同步到本地缓存中。存入数据时，先更新本地缓存，再同步更新到 Redis。
 * </p>
 */
public class LocalCacheAndRedissonService {

    private final LocalCacheService localCacheService;  // 本地缓存服务
    private final RedissionService redissionService;    // Redis 缓存服务

    /**
     * 构造函数
     *
     * @param localCacheService 本地缓存服务
     * @param redissionService  Redis 缓存服务
     */
    public LocalCacheAndRedissonService(LocalCacheService localCacheService, RedissionService redissionService) {
        this.localCacheService = localCacheService;
        this.redissionService = redissionService;
    }

    /**
     * 根据给定的 key 获取缓存数据
     * <p>
     * 先从本地缓存中获取数据，如果本地缓存未命中，则从 Redis 中获取。如果从 Redis 中获取成功，则将数据同步到本地缓存。
     * </p>
     *
     * @param key 缓存的 key
     * @return 缓存的值，如果缓存中没有，返回 null
     */
    public Object get(String key) {
        // 先从本地缓存获取
        Object value = localCacheService.get(key);
        if (value != null) {
            return value; // 本地缓存命中，返回数据
        }

        // 如果本地缓存未命中，则从 Redis 获取
        value = redissionService.getObject(key);
        if (value != null) {
            // 将 Redis 中的数据缓存到本地缓存
            localCacheService.put(key, value);
        }

        return value;
    }

    /**
     * 将数据存入缓存
     * <p>
     * 先将数据存入本地缓存，然后同步更新到 Redis 缓存中
     * </p>
     *
     * @param key   缓存的 key
     * @param value 缓存的值
     */
    public void put(String key, Object value) {
        // 先存入本地缓存
        localCacheService.put(key, value);
        // 同步存入 Redis
        redissionService.setObject(key, value);
    }
}
