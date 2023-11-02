package com.atguigu.redis.redlock.config;

import lombok.Data;

@Data
public class RedisPoolProperties {

    private int maxIdle;

    private int minIdle;

    private int maxActive;

    private int maxWait;

    private int connTimeout = 10000;

    private int soTimeout;

    /**
     * 池大小
     */
    private  int size;

}