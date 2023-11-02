package com.atguigu.redis7.service;

import cn.hutool.core.date.DateUtil;
import com.atguigu.redis7.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @auther zzyy
 * @create 2022-12-31 14:26
 */
@Service
@Slf4j
public class JHSTaskService
{
    public  static final String JHS_KEY="jhs";
    public  static final String JHS_KEY_A="jhs:a";
    public  static final String JHS_KEY_B="jhs:b";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 偷个懒不加mybatis了，模拟从数据库读取20件特价商品，用于加载到聚划算的页面中
     * @return
     */
    private List<Product> getProductsFromMysql() {
        List<Product> list=new ArrayList<>();
        for (int i = 1; i <=20; i++) {
            Random rand = new Random();
            int id= rand.nextInt(10000);
            Product obj=new Product((long) id,"product"+i,i,"detail");
            list.add(obj);
        }
        return list;
    }

    //@PostConstruct
    public void initJHS()
    {
        log.info("启动定时器天猫聚划算功能模拟开始......,O(∩_∩)O哈哈~");

        //1 用线程模拟定时任务，后台任务定时将mysql里面的参加活动的商品刷新到redis里
        new Thread(() -> {
            while (true)
            {
                //2 模拟从mysql查出数据，用于加载到redis并给聚划算页面显示
                List<Product> list = this.getProductsFromMysql();
                //3 采用redis list数据结构的lpush命令来实现存储
                redisTemplate.delete(JHS_KEY);
                //4 加入最新的数据给redis参加活动
                redisTemplate.opsForList().leftPushAll(JHS_KEY,list);
                //5 暂停1分钟线程，间隔一分钟执行一次，模拟聚划算一天执行的参加活动的品牌
                try { TimeUnit.MINUTES.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        },"t1").start();

    }

    @PostConstruct
    public void initJHSAB(){
        log.info("启动AB定时器计划任务天猫聚划算功能模拟.........."+DateUtil.now());

        //1 用线程模拟定时任务，后台任务定时将mysql里面的参加活动的商品刷新到redis里
        new Thread(() -> {
            while (true)
            {
                //2 模拟从mysql查出数据，用于加载到redis并给聚划算页面显示
                List<Product> list = this.getProductsFromMysql();
                //3 先更新B缓存且让B缓存过期时间超过A缓存，如果A突然失效了还有B兜底，防止击穿
                redisTemplate.delete(JHS_KEY_B);
                redisTemplate.opsForList().leftPushAll(JHS_KEY_B,list);
                redisTemplate.expire(JHS_KEY_B,86410L,TimeUnit.SECONDS);
                //4 再更新A缓存
                redisTemplate.delete(JHS_KEY_A);
                redisTemplate.opsForList().leftPushAll(JHS_KEY_A,list);
                redisTemplate.expire(JHS_KEY_A,86400L,TimeUnit.SECONDS);

                //5 暂停1分钟线程，间隔一分钟执行一次，模拟聚划算一天执行的参加活动的品牌
                try { TimeUnit.MINUTES.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        },"t1").start();
    }
}
