package com.spongehah.redis7.demo;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class JedisDemo {
    public static void main(String[] args) {
        //1 connection获得，通过指定ip和端口号
        Jedis jedis = new Jedis("192.168.111.100", 6379);

        //2 指定访问服务器的密码
        jedis.auth("zw2635879218@");

        //3 获得了jedis客户端，可以像jdbc一样，访问我们的jedis
        System.out.println(jedis.ping());

        //keys
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);

        //String
        jedis.set("k3", "hello-jedis");
        System.out.println(jedis.get("k3"));
        jedis.expire("k3",20);
        System.out.println(jedis.ttl("k3"));

        //list
        jedis.lpush("list1", "11","22","33","44");
        List<String> list1 = jedis.lrange("list1", 0, -1);
        for (String element : list1) {
            System.out.println(element);
        }


    }
}
