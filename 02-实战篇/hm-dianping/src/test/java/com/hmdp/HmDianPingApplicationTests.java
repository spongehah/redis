package com.hmdp;

import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class HmDianPingApplicationTests {
    
    @Resource
    private ShopServiceImpl shopService;
    
    @Resource
    private RedisIdWorker redisIdWorker;
    
    private ExecutorService threadPool = Executors.newFixedThreadPool(500);
    
    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int j = 0; j < 100; j++) {
                long id = redisIdWorker.nextId("order");
                System.out.println(id);
            }
            countDownLatch.countDown();
        };    
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            threadPool.submit(task);
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("----costTime：" + (endTime - startTime) + " 毫秒");
        
    }
    
    @Test
    void testSaveExpire() throws InterruptedException {
        shopService.saveShopRedis(1L,10L);
    }


}
