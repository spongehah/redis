package com.atguigu.redis7.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @auther zzyy
 * @create 2022-12-25 11:56
 */
@Service
@Slf4j
public class HyperLogLogService
{
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 模拟后台有用户点击淘宝首页www.taobao.com，每个用户来自不同的IP地址
     */
    //@PostConstruct
    public void initIP()
    {
        new Thread(() -> {
            String ip = null;
            for (int i = 0; i < 200; i++) {
                Random random = new Random();
                ip = random.nextInt(256)+"."+
                        random.nextInt(256)+"."+
                        random.nextInt(256)+"."+
                        random.nextInt(256);

                Long hll = redisTemplate.opsForHyperLogLog().add("hll", ip);
                log.info("ip={},该IP地址访问首页的次数={}",ip,hll);
                //暂停3秒钟
                try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        },"t1").start();
    }

    public long uv()
    {
        //PFCOUNT
        return redisTemplate.opsForHyperLogLog().size("hll");
    }

}

