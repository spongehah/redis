package com.atguigu.redis7.demo;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * @auther zzyy
 * @create 2022-12-13 20:12
 */

public class JedisDemo
{
    public static void main(String[] args)
    {
        //1 connection获得，通过指定ip和端口号
        Jedis jedis = new Jedis("192.168.111.185", 6379);
        //2 指定访问服务器的密码
        jedis.auth("111111");
        //3 获得了jedis客户端，可以像jdbc一样，访问我们的redis
        System.out.println(jedis.ping());

        //keys
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);

        //string
        jedis.set("k3","hello-jedis");
        System.out.println(jedis.get("k3"));
        System.out.println(jedis.ttl("k3"));
        jedis.expire("k3",20L);

        //list
        jedis.lpush("list","11","12","13");
        List<String> list = jedis.lrange("list", 0, -1);
        for (String element : list) {
            System.out.println(element);
        }
        //家庭作业

    }
}
