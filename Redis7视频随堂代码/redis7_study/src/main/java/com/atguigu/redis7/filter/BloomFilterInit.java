package com.atguigu.redis7.filter;

import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.managed.ManagedTransaction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @auther zzyy
 * @create 2022-12-27 14:55
 * 布隆过滤器白名单初始化工具类，一开始就设置一部分数据为白名单所有，
 * 白名单业务默认规定：布隆过滤器有，redis是极大可能有。
 * 白名单：whitelistCustomer
 */
@Component
@Slf4j
public class BloomFilterInit
{
    @Resource
    private RedisTemplate redisTemplate;

    //@PostConstruct//初始化白名单数据,暂时注释省的后台打印
    public void init()
    {
        //1 白名单客户加载到布隆过滤器
        String key = "customer:12";
        //2 计算hashValue,由于存在计算出来负数的可能，我们取绝对值
        int hashValue = Math.abs(key.hashCode());
        //3 通过hashValue和2的32次方后取余，获得对应的下标坑位
        long index = (long)(hashValue % Math.pow(2,32));
        log.info(key+" 对应的坑位index:{}",index);
        //4 设置redis里面的bitmap对应类型白名单：whitelistCustomer的坑位，将该值设置为1
        redisTemplate.opsForValue().setBit("whitelistCustomer",index,true);

    }
}
