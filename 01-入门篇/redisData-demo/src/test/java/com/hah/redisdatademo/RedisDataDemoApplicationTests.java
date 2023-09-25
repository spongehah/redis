package com.hah.redisdatademo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hah.redisdatademo.pojo.User;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisDataDemoApplicationTests {
    
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    
    

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("name","鸡哥");
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println("name = " + name);
    }
    
    @Test
    void testSaveUser(){
        //写入数据
        redisTemplate.opsForValue().set("user:100",new User("鸡哥",21));
        //获取数据
        User user = (User) redisTemplate.opsForValue().get("user:100");
        System.out.println("user = " + user);
    }
    
   

}
