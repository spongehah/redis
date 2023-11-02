package com.atguigu.redis.redlock.controller;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Slf4j
public class RedLockController
{
    public static final String CACHE_KEY_REDLOCK = "ATGUIGU_REDLOCK";

    @Autowired RedissonClient redissonClient1;
    @Autowired RedissonClient redissonClient2;
    @Autowired RedissonClient redissonClient3;

    @GetMapping(value = "/multilock")
    public String getMultiLock()
    {
        String taskThreadID = Thread.currentThread().getId()+"";

        RLock lock1 = redissonClient1.getLock(CACHE_KEY_REDLOCK);
        RLock lock2 = redissonClient2.getLock(CACHE_KEY_REDLOCK);
        RLock lock3 = redissonClient3.getLock(CACHE_KEY_REDLOCK);

        RedissonMultiLock redLock = new RedissonMultiLock(lock1, lock2, lock3);

        redLock.lock();
        try
        {
            log.info("come in biz multilock:{}",taskThreadID);
            try { TimeUnit.SECONDS.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }
            log.info("task is over multilock:{}",taskThreadID);
        }catch (Exception e){
            e.printStackTrace();
            log.error("multilock exception:{}",e.getCause()+"\t"+e.getMessage());
        }finally {
            redLock.unlock();
            log.info("释放分布式锁成功key:{}",CACHE_KEY_REDLOCK);
        }
        return "multilock task is over: "+taskThreadID;
    }
}