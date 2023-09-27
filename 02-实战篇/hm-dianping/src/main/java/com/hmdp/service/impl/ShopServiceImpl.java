package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisCacheHelper;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisData;
import io.netty.util.internal.StringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisCacheHelper redisCacheHelper;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    @Override
    public Result queryById(Long id) {
        // 缓存穿透
        // return queryWithPassThrough(id);


        /**
         * 互斥锁解决缓存击穿
         */
        // return queryWithMutex(id);

        // 使用封装好的工具类 缓存穿透
        Shop shop = redisCacheHelper.queryWithPassThrough(RedisConstants.CACHE_SHOP_KEY, id, Shop.class, this::getById,
                RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        if (shop == null) {
            return Result.fail("店铺信息不存在！");
        }
        return Result.ok(shop);


        /**
         * 逻辑过期解决缓存击穿
         */
        // return queryWithLogicalExpire(id);
       /* Shop shop =  redisCacheHelper.queryWithLogicalExpire(RedisConstants.CACHE_SHOP_KEY, id, Shop.class, this::getById, 20L, TimeUnit.SECONDS, RedisConstants.LOCK_SHOP_KEY);
        if (shop == null) {
            return Result.fail("店铺信息不存在！");
        }
        return Result.ok(shop);*/
    }

    /**
     * 逻辑过期解决缓存击穿
     *
     * @param id
     * @return
     */
    private Result queryWithLogicalExpire(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        // 1 从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // TODO 2 判断是否存在
        if (StrUtil.isBlank(shopJson)) {
            // 3 不存在，直接返回错误信息
            return Result.fail("店铺信息不存在！");
        }

        // TODO 4 命中，需要把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        JSONObject jsonObject = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(jsonObject, Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        // TODO 5 判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // TODO 5.1 未过期，直接返回店铺信息
            return Result.ok(shop);
        }
        // TODO 5.2 已过期，需要缓存重建
        // TODO 6 缓存重建
        // TODO 6.1 获取互斥锁
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        boolean isLock = this.tryLock(lockKey);
        // TODO 6.2 判断是否获取锁成功
        if (isLock) {
            // TODO 可以在这里再进行一个double check，如果redis缓存依旧没有数据的话，才查询数据库
            // TODO 6.3 成功，开启独立线程，实现缓存重建你
            CompletableFuture.runAsync(() -> {
                try {
                    this.saveShopRedis(id, 20L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // TODO 释放互斥锁
                    this.unlock(lockKey);
                }
            }, CACHE_REBUILD_EXECUTOR);
        }
        // TODO 6.4 无论成功或失败，返回过期的商铺信息
        return Result.ok(shop);
    }

    /**
     * （在缓存穿透基础上）缓存击穿解决方案：1 互斥锁方案
     *
     * @param id
     * @return
     */
    private Result queryWithMutex(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        while (true) {
            // 1 从redis查询商铺缓存
            String shopJson = stringRedisTemplate.opsForValue().get(key);
            // 2 判断是否存在
            if (StrUtil.isNotBlank(shopJson)) {
                // 3 存在，直接返回
                Shop shop = JSONUtil.toBean(shopJson, Shop.class);
                return Result.ok(shop);
            }
            /**
             * isNotBlank()方法会检测到空字符串
             * 为解决缓存穿透，额外判断是否为""空字符串，是则返回错误信息
             */
            if (shopJson != null) {
                return Result.fail("店铺不存在！");
            }

            // TODO 4 实现缓存重建
            // TODO 4.1 获取互斥锁
            boolean isLock = tryLock(lockKey);
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
            Shop shop = this.getById(id);
            // 模拟缓存击穿重建业务耗时久情况
            Thread.sleep(200);
            // 5 数据库不存在，返回错误
            if (shop == null) {
                /**
                 * 为解决缓存穿透，将空值写入redis
                 */
                stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return Result.fail("店铺不存在！");
            }
            // 6  存在，写入redis
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);

            // 7 存在，返回数据
            return Result.ok(shop);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // TODO 7 释放互斥锁
            unlock(lockKey);
        }
    }


    /**
     * 这是queryById的解决了双写问题和缓存穿透的方法，
     * 提取出来做备份，上面重新写解决缓存击穿的方法
     *
     * @param id
     * @return
     */
    private Result queryWithPassThrough(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        // 1 从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2 判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3 存在，直接返回
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        /**
         * isNotBlank()方法会检测到空字符串
         * 为解决缓存穿透，额外判断是否为""空字符串，是则返回错误信息
         */
        if (shopJson != null) {
            return Result.fail("店铺不存在！");
        }

        // 4 不存在，根据id查询数据库
        Shop shop = this.getById(id);
        // 5 数据库不存在，返回错误
        if (shop == null) {
            /**
             * 为解决缓存穿透，将空值写入redis
             */
            stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Result.fail("店铺不存在！");
        }
        // 6 存在，写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 7 存在，返回数据
        return Result.ok(shop);
    }

    private boolean tryLock(String key) {
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstants.LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(isLock);  // 包装类会做拆箱可能出现空指针，于是主动拆箱
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 设置逻辑过期缓存shop到redis
     *
     * @param id
     * @param expireSeconds
     */
    public void saveShopRedis(Long id, Long expireSeconds) throws InterruptedException {
        Thread.sleep(200);
        // 1 查询店铺数据
        Shop shop = this.getById(id);
        // 2 封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 3 写入redis
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        // 1 更新数据库
        this.updateById(shop);
        // 2 删除缓存
        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + id);
        return Result.ok();
    }
}
