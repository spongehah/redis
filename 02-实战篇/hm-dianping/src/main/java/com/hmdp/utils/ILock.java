package com.hmdp.utils;

public interface ILock {

    /**
     * 尝试获取锁
     * @param timeoutSec 锁持有的超时时间（秒），过期后自动释放
     * @return true代表获取锁成功；false代表获取锁失败
     */
    boolean tryLock(Long timeoutSec);

    /**
     * 释放锁
     */
    void unlock();
}
