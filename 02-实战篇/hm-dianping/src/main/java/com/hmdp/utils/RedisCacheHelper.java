package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Redis做缓存时使用的工具包
 * 可以解决以下问题：
 *      方法1：将任意Java对象序列化为json并存储在string类型的key中，并且可以设置TTL过期时间(为了**双写一致兜底**)
 *      方法2：将任意Java对象序列化为json并存储在string类型的key中，并且可以设置**逻辑过期时间**，用于处理**缓存击穿**问题
 *      方法3：根据指定的key查询缓存，并反序列化为指定类型，利用缓存空值的方式解决**缓存穿透**问题
 *      方法4：根据指定的key查询缓存，并反序列化为指定类型，需要利用逻辑过期解决**缓存击穿**问题
 *      方法5：根据指定的key查询缓存，并反序列化为指定类型，需要利用互斥锁解决**缓存击穿**问题
 *          
 * 注意事项：需要引入hutool依赖，并且需要RedisData类
 * <!--hutool-->
 * <dependency>
 *     <groupId>cn.hutool</groupId>
 *     <artifactId>hutool-all</artifactId>
 *     <version>5.7.17</version>
 * </dependency>
 * 
 * @Data
 * public class RedisData {
 *     private LocalDateTime expireTime;
 *     private Object data;
 * }
 * 
 * @author spongehah from hut
 * @since 2023-09-24
 */

@Component
@Slf4j
public class RedisCacheHelper {
    
    private final StringRedisTemplate stringRedisTemplate;

    // 使用构造注入，也可以使用自动注入
    public RedisCacheHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 方法1：将任意Java对象序列化为json并存储在string类型的key中，并且可以设置TTL过期时间(为了**双写一致兜底**)
     * @param key key
     * @param value value
     * @param time TTL过期时间
     * @param unit TTL过期时间单位
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 方法2：将任意Java对象序列化为json并存储在string类型的key中，并且可以设置**逻辑过期时间**，用于处理**缓存击穿**问题
     * @param key key
     * @param value value
     * @param expireTime TTL逻辑过期时间
     * @param unit TTL过期时间单位
     */
    public void setWithLogicalExpire(String key, Object value, Long expireTime, TimeUnit unit) {
        //设置逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(expireTime)));
        //写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 方法3：根据指定的key查询缓存，并反序列化为指定类型，利用缓存空值的方式解决**缓存穿透**问题
     * 非热点数据，一般情况下使用，解决缓存穿透即可
     * @param keyPrefix key前缀，key = keyPrefix + id
     * @param id 查询对象id
     * @param type 查询对象类型
     * @param dbFallback 查询数据库操作函数
     * @param time 新放入Redis对象的TTL过期时间
     * @param unit 新放入Redis对象的TTL过期时间单位
     * @return 查询的对象
     * @param <R> 查询对象类型
     * @param <ID> 查询对象id的类型
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback,
                                           Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1 从redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            // 3 存在，直接返回
            return JSONUtil.toBean(json, type);
        }
        /**
         * isNotBlank()方法会检测到空字符串
         * 为解决缓存穿透，额外判断是否为""空字符串，是则返回null
         */
        if (json != null) {
            return null;
        }

        // 4 不存在，根据id查询数据库
        R r = dbFallback.apply(id);
        // 5 数据库不存在，返回错误
        if (r == null) {
            /**
             * 为解决缓存穿透，将空值写入redis，2分钟后过期（可自己修改）
             */
            stringRedisTemplate.opsForValue().set(key, "", 2L, TimeUnit.MINUTES);
            //返回空值
            return null;
        }
        // 6 存在，写入redis
        this.set(key, r, time, unit);
        // 7 存在，返回数据
        return r;
    }

    //自定义线程池，用于queryWithLogicalExpire方法，可根据自己需求更改
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = new ThreadPoolExecutor(
            3,
            7,
            3L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 方法4：根据指定的key查询缓存，并反序列化为指定类型，需要利用逻辑过期解决**缓存击穿**问题
     * 热点数据，使用逻辑过期解决缓存击穿问题
     * 提前手动缓存热点数据，查询缓存为null直接返回null，不需要解决缓存穿透
     * @param keyPrefix key前缀，key = keyPrefix + id
     * @param id 查询对象id
     * @param type 查询对象类型
     * @param dbFallback 查询数据库操作函数
     * @param time 新放入Redis对象的TTL过期时间
     * @param unit 新放入Redis对象的TTL过期时间单位
     * @param lockKeyPrefix Redis缓存重建的key
     * @return 查询的对象
     * @param <R> 查询对象类型
     * @param <ID> 查询对象id的类型
     */
    public <R, ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback,
                                             Long time, TimeUnit unit, String lockKeyPrefix) {
        String key = keyPrefix + id;
        // 1 从redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // TODO 2 判断是否存在
        if (StrUtil.isBlank(json)) {
            // 3 不存在，直接返回null
            return null;
        }

        // TODO 4 命中，需要把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        JSONObject jsonObject = (JSONObject) redisData.getData();
        R r = JSONUtil.toBean(jsonObject, type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // TODO 5 判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // TODO 5.1 未过期，直接返回查询对象信息
            return r;
        }
        // TODO 5.2 已过期，需要缓存重建
        // TODO 6 缓存重建
        // TODO 6.1 获取互斥锁
        String lockKey = lockKeyPrefix + id;
        boolean isLock = this.tryLock(lockKey);
        // TODO 6.2 判断是否获取锁成功
        if (isLock) {
            // TODO 可以在这里再进行一个double check，如果redis缓存依旧没有数据的话，才查询数据库
            // TODO 6.3 成功，开启独立线程，实现缓存重建你
            CompletableFuture.runAsync(() -> {
                try {
                    //查询数据库
                    R r2 = dbFallback.apply(id);
                    //写入redis，带逻辑过期时间
                    this.setWithLogicalExpire(key, r2, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // TODO 释放互斥锁
                    this.unlock(lockKey);
                }
            }, CACHE_REBUILD_EXECUTOR);
        }
        // TODO 6.4 无论成功或失败，返回过期的对象信息
        return r;
    }

    /**
     * 方法5：根据指定的key查询缓存，并反序列化为指定类型，需要利用互斥锁解决**缓存击穿**问题
     * 热点数据，使用互斥锁解决缓存击穿问题
     * 需要同时解决缓存穿透问题
     * @param keyPrefix key前缀，key = keyPrefix + id
     * @param id 查询对象id
     * @param type 查询对象类型
     * @param dbFallback 查询数据库操作函数
     * @param time 新放入Redis对象的TTL过期时间
     * @param unit 新放入Redis对象的TTL过期时间单位
     * @param lockKeyPrefix Redis缓存重建的key
     * @return 查询的对象
     * @param <R> 查询对象类型
     * @param <ID> 查询对象id的类型
     */
    public <R, ID> R queryWithMutex(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback,
                                Long time, TimeUnit unit, String lockKeyPrefix) {
        String key = keyPrefix + id;
        String lockKey = lockKeyPrefix + id;
        while (true) {
            // 1 从redis查询商铺缓存
            String json = stringRedisTemplate.opsForValue().get(key);
            // 2 判断是否存在
            if (StrUtil.isNotBlank(json)) {
                // 3 存在，直接返回
                R r = JSONUtil.toBean(json, type);
                return r;
            }
            /**
             * isNotBlank()方法会检测到空字符串
             * 为解决缓存穿透，额外判断是否为""空字符串，是则返回null
             */
            if (json != null) {
                return null;
            }

            // TODO 4 实现缓存重建
            // TODO 4.1 获取互斥锁
            boolean isLock = this.tryLock(lockKey);
            // TODO 4.2 判断是否获取成功
            if (!isLock) {
                // TODO 4.3 失败：则休眠重试
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        try {
            // TODO 可以在这里再进行一个double check，如果redis缓存依旧没有数据的话，才查询数据库

            // TODO 4.4 成功，根据id查询数据库
            R r = dbFallback.apply(id);
            // 模拟缓存击穿重建业务耗时久情况
            Thread.sleep(200);
            // 5 数据库不存在，返回错误
            if (r == null) {
                /**
                 * 为解决缓存穿透，将空值写入redis
                 */
                stringRedisTemplate.opsForValue().set(key, "", 2L, TimeUnit.MINUTES);
                return null;
            }
            // 6  存在，写入redis
            this.set(key, r, time, unit);

            // 7 存在，返回数据
            return r;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // TODO 7 释放互斥锁
            this.unlock(lockKey);
        }
    }

    //互斥锁简单实现，获得锁，queryWithLogicalExpire和queryWithMutex方法内调用
    private boolean tryLock(String key) {
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10L, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(isLock);  // 包装类会做拆箱可能出现空指针，于是主动拆箱
    }
    //互斥锁简单实现，释放锁，queryWithLogicalExpire和queryWithMutex方法内调用
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
    
}
