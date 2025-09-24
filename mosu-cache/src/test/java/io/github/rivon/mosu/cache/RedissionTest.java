package io.github.rivon.mosu.cache;

import io.github.rivon.mosu.cache.service.RedissionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

/**
 * 测试Redission缓存服务
 * <p>
 * 开发者需要确保：
 * 已经配置好redission依赖，并且cache.properties已经设置正确
 */
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RedissionTest {

    @Resource
    private RedissionService redissionService;

    // 测试字符串缓存
    @Test
    public void testRedissionSetString() {
        redissionService.setString("testString", "Hello, Redisson!");
    }

    // 测试字符串缓存
    @Test
    public void testRedissionGetString() {
        String value = redissionService.getString("testString");
        System.out.println(value); // 输出缓存值
    }

    // 测试对象缓存
    @Test
    public void testRedissionSetObject() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "John");
        user.put("age", "30");
        redissionService.setObject("user:123", user);
    }

    // 测试对象缓存
    @Test
    public void testRedissionGetObject() {
        Map<String, String> user = redissionService.getObject("user:123");
        System.out.println(user.get("name")); // 输出 user 的 name
    }

    // 测试列表缓存
    @Test
    public void testRedissionSetList() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        redissionService.setList("names", names);
    }

    // 测试列表缓存
    @Test
    public void testRedissionGetList() {
        List<String> names = redissionService.getList("names", String.class);
        names.forEach(System.out::println); // 输出 List 中的每个名字
    }

    // 测试集合缓存
    @Test
    public void testRedissionSetSet() {
        Set<String> uniqueNames = new HashSet<>(Arrays.asList("Alice", "Bob", "Charlie"));
        redissionService.setSet("uniqueNames", uniqueNames);
    }

    // 测试集合缓存
    @Test
    public void testRedissionGetSet() {
        Set<String> uniqueNames = redissionService.getSet("uniqueNames");
        uniqueNames.forEach(System.out::println); // 输出 Set 中的每个名字
    }

    // 测试Map缓存
    @Test
    public void testRedissionSetMap() {
        Map<String, Integer> ageMap = new HashMap<>();
        ageMap.put("Alice", 30);
        ageMap.put("Bob", 25);
        redissionService.setMap("ageMap", ageMap);
    }

    // 测试Map缓存
    @Test
    public void testRedissionGetMap() {
        Map<String, Integer> ageMap = redissionService.getMap("ageMap", String.class, Integer.class);
        ageMap.forEach((key, value) -> System.out.println(key + ": " + value)); // 输出 Map 中的键值对
    }

    // 测试Json缓存
    @Test
    public void testRedissionSetJson() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "John");
        user.put("age", "30");
        redissionService.setJson("user", user);
    }

    // 测试json缓存
    @Test
    public void testRedissionGetJson() {
        Map<String, String> user = redissionService.getJson("user", Map.class);
        System.out.println(user.get("name")); // 输出 user 的 name
    }

    // 测试JsonArray缓存
    @Test
    public void testRedissionJsonArray() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        redissionService.setJsonArray(  "names", names);
    }

    // 测试JsonArray缓存
    @Test
    public void testRedissionGetJsonArray() {
        List<String> names = redissionService.getJsonArray("names", String.class);
        names.forEach(System.out::println); // 输出 List 中的每个名字
    }

    // 测试JsonArray缓存
    @Test
    public void testRedissionSetWithExpire() {
        redissionService.setWithExpire("testExpire", "This is expired data", 10); // 10 秒后过期
    }

    // 测试删除缓存
    @Test
    public void testRedissionGetAndCheck() {
        try {
            String value = redissionService.getAndCheck("testExpire");
            System.out.println(value);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage()); // 如果没有找到缓存，抛出异常
        }
    }

    // 测试分布式锁
    @Test
    public void testTryLock() {
        boolean isLockAcquired = redissionService.tryLock("lockKey");
        System.out.println("Lock acquired: " + isLockAcquired);
    }

    // 测试分布式锁
    @Test
    public void testTryLockWithTimeout() {
        boolean isLockAcquired = redissionService.tryLock("lockKeyWithTimeout", 10, 30); // 10 秒等待，30 秒锁过期
        System.out.println("Lock acquired with timeout: " + isLockAcquired);
    }
}
