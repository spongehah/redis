package com.heima.item.canal;


import com.github.benmanes.caffeine.cache.Cache;
import com.heima.item.config.RedisHandler;
import com.heima.item.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Component
@CanalTable("tb_item")
public class ItemHandler implements EntryHandler<Item> {
    
    @Autowired
    private Cache<Long, Item> itemCache;
    
    @Autowired
    private RedisHandler redisHandler;

    @Override
    public void insert(Item item) {
        // 写数据到JVM缓存
        itemCache.put(item.getId(), item);
        // 写数据到redis
        redisHandler.saveItem(item);
        
    }

    @Override
    public void update(Item before, Item after) {
        // 写数据到JVM缓存
        itemCache.put(after.getId(), after);
        // 写数据到redis
        redisHandler.saveItem(after);
    }

    @Override
    public void delete(Item item) {
        // 删除数据到JVM缓存
        itemCache.invalidate(item.getId());
        // 删除数据到redis
        redisHandler.deleteItem(item.getId());
    }
}
