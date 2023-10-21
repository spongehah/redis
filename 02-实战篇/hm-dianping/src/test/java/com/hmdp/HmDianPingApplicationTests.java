package com.hmdp;

import com.hmdp.entity.Shop;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.SHOP_GEO_KEY;

@SpringBootTest
class HmDianPingApplicationTests {
    
    @Resource
    private ShopServiceImpl shopService;
    
    @Resource
    private RedisIdWorker redisIdWorker;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
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
    
    @Test
    void loadShopData() {
        List<Shop> shops = shopService.list();
        Map<Long, List<Shop>> map = shops.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {
            Long typeId = entry.getKey();
            String key = SHOP_GEO_KEY + typeId;
            List<Shop> value = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(value.size());
            for (Shop shop : value) {
                locations.add(new RedisGeoCommands.GeoLocation<>(shop.getId().toString(), new Point(shop.getX(), shop.getY())));
            }
            stringRedisTemplate.opsForGeo().add(key, locations);
        }
    }


}
