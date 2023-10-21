package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 开始时间戳
     */
    public static final long BEGIN_TIMESTAMP = 1640995299L;//2021年1月1日时间戳

    /**
     * 序列号的位数
     */
    public static final int COUNT_BITS = 32;

    /**
     * 雪花算法实现，1位符号位 + 31位时间戳 + 32位序列号
     * @param keyPrefix
     * @return
     */
    public long nextId(String keyPrefix) {
        //1 生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;
        
        //2 生成序列号
        //2.1 获取当前日期，精确到天
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        //2.2 自增长
        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
        if (count == 1L) {
            stringRedisTemplate.expire("icr:" + keyPrefix + ":" + date, Duration.ofDays(1L));
        }

        //拼接并返回
        return timestamp << COUNT_BITS | count;
    }
}
