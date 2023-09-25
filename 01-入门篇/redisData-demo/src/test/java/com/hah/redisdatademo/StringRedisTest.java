package com.hah.redisdatademo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hah.redisdatademo.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.Map;

@SpringBootTest
public class StringRedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void testStringRedisTemplate() throws JsonProcessingException {
        // 创建对象
        User user = new User("鸡哥",21);
        // 手动序列化
        String json = OBJECT_MAPPER.writeValueAsString(user);

        // 写入数据
        stringRedisTemplate.opsForValue().set("user:200",json);
        // 获取数据
        String jsonUser = stringRedisTemplate.opsForValue().get("user:200");
        // 手动反序列化
        User user1 = OBJECT_MAPPER.readValue(jsonUser, User.class);
        System.out.println("user1 = " + user1);
    }
    
    @Test
    void testHash(){
        stringRedisTemplate.opsForHash().put("user:400","name","鸡哥");
        stringRedisTemplate.opsForHash().put("user:400", "age","18");

        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("user:400");
        System.out.println(entries);
        for (Map.Entry<Object, Object> objectObjectEntry : entries.entrySet()) {
            System.out.println(objectObjectEntry.getKey() + ": " + objectObjectEntry.getValue());
        }

    }
}
