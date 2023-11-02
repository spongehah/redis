package com.heima.item.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.heima.item.pojo.Item;
import com.heima.item.pojo.ItemStock;
import com.heima.item.service.IItemService;
import com.heima.item.service.IItemStockService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisHandler implements InitializingBean {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private IItemService itemService;
    
    @Autowired
    private IItemStockService stockService;
    
    public static final JsonMapper MAPPER = new JsonMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Item> itemList = itemService.list();
        for (Item item : itemList) {
            String itemJSON = MAPPER.writeValueAsString(item);
            stringRedisTemplate.opsForValue().set("item:id:" + item.getId(), itemJSON);
        }

        List<ItemStock> stockList = stockService.list();
        for (ItemStock stock : stockList) {
            String stockJSON = MAPPER.writeValueAsString(stock);
            stringRedisTemplate.opsForValue().set("item:stock:id:" + stock.getId(), stockJSON);
        }
    }
    
    public void saveItem(Item item){
        try {
            String itemJSON = MAPPER.writeValueAsString(item);
            stringRedisTemplate.opsForValue().set("item:id:" + item.getId(), itemJSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void deleteItem(Long id){
        stringRedisTemplate.delete("item:id:" + id);
    }
}
