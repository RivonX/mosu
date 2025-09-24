package io.github.rivon.mosu.cache;

import io.github.rivon.mosu.cache.service.LocalCacheService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试本地缓存服务
 * <p>
 * 开发者需要确保：
 * 已经配置好localCache依赖，并且cache.properties已经设置正确
 */
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LocalCacheTest {

    @Resource
    private LocalCacheService localCacheService;

    // 测试缓存放入与获取
    @Test
    public void testPutAndGet() {
        String key = "testKey";
        String value = "testValue";

        // 放入缓存
        localCacheService.put(key, value);

        // 获取缓存
        Object cachedValue = localCacheService.get(key);
        assertEquals(value, cachedValue); // 校验缓存值
    }

    // 测试缓存过期机制
    @Test
    public void testCacheExpiry() throws InterruptedException {
        String key = "testExpireKey";
        String value = "testExpireValue";

        // 放入缓存
        localCacheService.put(key, value);

        // 确认缓存存在
        assertNotNull(localCacheService.get(key));

        // 等待过期时间
        TimeUnit.SECONDS.sleep(6); // 等待超过过期时间（5秒）

        // 确认缓存已过期
        assertNull(localCacheService.get(key));
    }

    // 测试批量放入缓存
    @Test
    public void testPutAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        // 批量放入缓存
        localCacheService.putAll(map);

        // 确认缓存存在
        assertEquals("value1", localCacheService.get("key1"));
        assertEquals("value2", localCacheService.get("key2"));
    }

    // 测试缓存移除
    @Test
    public void testInvalidate() {
        String key = "testInvalidateKey";
        String value = "testInvalidateValue";

        // 放入缓存
        localCacheService.put(key, value);

        // 确认缓存存在
        assertNotNull(localCacheService.get(key));

        // 移除缓存
        localCacheService.invalidate(key);

        // 确认缓存已移除
        assertNull(localCacheService.get(key));
    }

    // 测试批量移除缓存
    @Test
    public void testInvalidateAll() {
        String key1 = "testInvalidateKey1";
        String key2 = "testInvalidateKey2";
        localCacheService.put(key1, "value1");
        localCacheService.put(key2, "value2");

        // 批量移除缓存
        localCacheService.invalidateAll();

        // 确认缓存已被清除
        assertNull(localCacheService.get(key1));
        assertNull(localCacheService.get(key2));
    }

    // 测试缓存模板方法
    @Test
    public void testCacheTemplateMethod() {
        String key = "testTemplateKey";

        // 使用缓存模板方法放入缓存
        String value = localCacheService.put(key, k -> "GeneratedValue");

        // 确认缓存值
        assertEquals("GeneratedValue", value);

        // 确认缓存中获取到值
        assertEquals("GeneratedValue", localCacheService.get(key));
    }

    // 测试缓存类型转换
    @Test
    public void testGetIfPresentWithType() {
        String key = "testTypeKey";
        String value = "testTypeValue";

        // 放入缓存
        localCacheService.put(key, value);

        // 获取并转换类型
        String cachedValue = localCacheService.getIfPresent(key, String.class);

        // 确认类型转换正确
        assertEquals(value, cachedValue);
    }
}
