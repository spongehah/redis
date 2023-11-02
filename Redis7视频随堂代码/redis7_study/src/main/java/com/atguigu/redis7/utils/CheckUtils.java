package com.atguigu.redis7.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @auther zzyy
 * @create 2022-12-27 14:56
 */
@Component
@Slf4j
public class CheckUtils
{
    @Resource
    private RedisTemplate redisTemplate;

    public boolean checkWithBloomFilter(String checkItem,String key)
    {
        int hashValue = Math.abs(key.hashCode());
        long index = (long)(hashValue % Math.pow(2,32));
        boolean existOK = redisTemplate.opsForValue().getBit(checkItem,index);
        log.info("--->key:"+key+" 对应坑位下标index: "+index+" 是否存在："+existOK);

        return existOK;
    }
}
