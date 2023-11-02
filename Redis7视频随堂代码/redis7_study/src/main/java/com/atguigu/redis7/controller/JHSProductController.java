package com.atguigu.redis7.controller;

import com.atguigu.redis7.entities.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @auther zzyy
 * @create 2022-12-31 14:29
 */
@RestController
@Slf4j
@Api(tags = "聚划算商品列表接口")
public class JHSProductController
{
    public  static final String JHS_KEY="jhs";
    public  static final String JHS_KEY_A="jhs:a";
    public  static final String JHS_KEY_B="jhs:b";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询：在高并发的情况下，只能走redis查询，走db的话必定会把db打垮
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/pruduct/find",method = RequestMethod.GET)
    @ApiOperation("聚划算案例，每次1页每页5条显示")
    public List<Product> find(int page, int size) {
        List<Product> list=null;

        long start = (page - 1) * size;
        long end   = start + size - 1;

        try
        {
            // 采用redis list结构里面的lrang命令来实现加载和分页查询
            list = redisTemplate.opsForList().range(JHS_KEY,start,end);
            if(CollectionUtils.isEmpty(list))
            {
                //TODO 走mysql查询
            }
            log.info("参加活动的商家:{}",list);
        }catch (Exception e){
            // 出异常了，一般redis宕机了或者redis网络抖动导致timeout
            log.error("jhs exception:{}",e);
            e.printStackTrace();
            // ....再次查询mysql
        }
        return list;
    }


    @RequestMapping(value = "/pruduct/findab",method = RequestMethod.GET)
    @ApiOperation("AB双缓存架构，防止热点key突然失效")
    public List<Product> findAB(int page, int size) {
        List<Product> list=null;

        long start = (page - 1) * size;
        long end   = start + size - 1;

        try
        {
            list = redisTemplate.opsForList().range(JHS_KEY_A,start,end);
            if(CollectionUtils.isEmpty(list))
            {
                log.info("---A缓存已经过期失效或活动结束了，记得人工修改，B缓存继续顶着");
                list = redisTemplate.opsForList().range(JHS_KEY_B,start,end);
                if(CollectionUtils.isEmpty(list))
                {
                    //TODO 走mysql查询
                }
            }
        }catch (Exception e){
            // 出异常了，一般redis宕机了或者redis网络抖动导致timeout
            log.error("jhs exception:{}",e);
            e.printStackTrace();
            // ....再次查询mysql
        }

        return list;
    }

}
