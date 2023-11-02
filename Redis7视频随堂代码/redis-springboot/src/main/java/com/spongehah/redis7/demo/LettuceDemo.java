package com.spongehah.redis7.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.List;

public class LettuceDemo {

    public static void main(String[] args) {
        //1 使用构建起链式编程来builder我们RedisURI
        RedisURI uri = RedisURI.builder()
                .redis("192.168.111.100")
                .withPort(6379)
                .withAuthentication("default", "zw2635879218@")
                .build();
        
        //2 创建连接客户端
        RedisClient redisClient = RedisClient.create(uri);
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        
        //3 通过connect创建操作的command
        RedisCommands<String, String> commands = connect.sync();
        
        //=======biz==========================
        //keys
        List<String> keys = commands.keys("*");
        System.out.println(keys);
        
        //String
        commands.set("k5","hello-lettuce");
        System.out.println(commands.get("k5"));
        //=======biz==========================
        
        //4 各种关闭释放资源
        connect.close();
        redisClient.shutdown();

    }
}
