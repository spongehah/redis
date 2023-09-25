package com.hmdp.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Redis逻辑过期封装DTO
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
