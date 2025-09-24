package io.github.rivon.mosu.cache;

import io.github.rivon.mosu.cache.service.LocalCacheAndRedissonService;
import io.github.rivon.mosu.cache.service.LocalCacheService;
import io.github.rivon.mosu.cache.service.RedissionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 测试二级缓存服务
 */
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LocalCacheAndRedissonTest {

    @Resource
    private LocalCacheService localCacheService;
    @Resource
    private RedissionService redissionService;
    @Resource
    private LocalCacheAndRedissonService localCacheAndRedissonService;

    // 测试缓存放入与获取
    @Test
    public void testGet() {
        String key = "testKey";
        String value = "testValue";

        // 清除之前的数据，确保测试环境干净
        localCacheService.invalidate(key);
        redissionService.deleteSet(key);

        // 将数据存入本地缓存
        localCacheService.put(key, value);

        // 获取缓存时，应该从本地缓存中命中
        Object cachedValue = localCacheAndRedissonService.get(key);
        assertEquals(value, cachedValue); // 校验从本地缓存获取的数据

        // 从 Redis 获取，验证 Redis 中没有存储
        Object redisValue = redissionService.getObject(key);
        assertNull(redisValue); // 如果 Redis 中未存入数据，则应该为 null

        // 将数据存入 Redis 并再次获取
        redissionService.setObject(key, value);
        cachedValue = localCacheAndRedissonService.get(key);
        assertEquals(value, cachedValue); // 此时应该从 Redis 中获取，并存入本地缓存
    }


    // 测试从redis中获取数据，放入本地缓存，再从本地缓存获取
    @Test
    public void testGet2() {
        String key = "testKey";
        String value = "testValue";

        // 清除之前的数据，确保测试环境干净
        localCacheService.invalidate(key);
        redissionService.deleteSet(key);

        // 将数据存入redis缓存
        redissionService.setObject(key, value);

        // 获取缓存时，应该从本地缓存中命中
        Object cachedValue = localCacheAndRedissonService.get(key);
        assertEquals(value, cachedValue); // 校验从本地缓存获取的数据
    }


    // 测试缓存过期机制
    @Test
    public void testPut() {
        String key = "testKey";
        String value = "testValue";

        // 清除之前的数据，确保测试环境干净
        localCacheService.invalidate(key);
        redissionService.deleteString(key);

        // 使用 put 方法将数据存入缓存
        localCacheAndRedissonService.put(key, value);

        // 从本地缓存获取数据，校验是否存入本地缓存
        Object cachedValue = localCacheService.get(key);
        assertEquals(value, cachedValue); // 校验本地缓存是否存储成功

        // 从 Redis 获取数据，校验是否同步到 Redis
        cachedValue = redissionService.getObject(key);
        assertEquals(value, cachedValue); // 校验 Redis 是否存储成功
    }

}
