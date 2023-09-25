package com.hah.jedis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConnectionFactory {
    
    public static final JedisPool JEDIS_POOL;
    
    static {
        //配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWaitMillis(1000);
        //创建Jedis对象
        JEDIS_POOL = new JedisPool(poolConfig,"redis100",6379);
    }
    
    public static Jedis getJedis(){
        return  JEDIS_POOL.getResource();
    }
}
