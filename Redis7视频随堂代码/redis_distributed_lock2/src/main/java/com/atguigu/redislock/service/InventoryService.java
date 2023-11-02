package com.atguigu.redislock.service;

import cn.hutool.core.util.IdUtil;
import com.atguigu.redislock.mylock.DistributedLockFactory;
import com.atguigu.redislock.mylock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import javax.annotation.Resource;

/**
 * @auther zzyy
 * @create 2023-01-04 15:59
 */
@Service
@Slf4j
public class InventoryService
{
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${server.port}")
    private String port;
    @Autowired
    private DistributedLockFactory distributedLockFactory;


    //V9.1,引入Redisson对应的官网推荐RedLock算法实现类
    @Autowired
    private Redisson redisson;
    public String saleByRedisson()
    {
        String retMessage = "";

        RLock redissonLock = redisson.getLock("zzyyRedisLock");
        redissonLock.lock();

        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            //改进点，只能删除属于自己的key，不能删除别人的
            if(redissonLock.isLocked() && redissonLock.isHeldByCurrentThread())
            {
                redissonLock.unlock();
            }
        }
        return retMessage+"\t"+"服务端口号"+port;
    }







    //V8.0，实现自动续期功能的完善，后台自定义扫描程序，如果规定时间内没有完成业务逻辑，会调用加钟自动续期的脚本
    public String sale()
    {
        String retMessage = "";

        Lock redisLock = distributedLockFactory.getDistributedLock("redis");
        redisLock.lock();
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
                //暂停120秒钟线程,故意的，演示自动续期的功能。。。。。。
                try { TimeUnit.SECONDS.sleep(120); } catch (InterruptedException e) { e.printStackTrace(); }
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            redisLock.unlock();
        }
        return retMessage+"\t"+"服务端口号"+port;
    }


    //V7.0版本，如何将我们的lock/unlock+lua脚本自研版的redis分布式锁搞定？
    /*public String sale()
    {
        String retMessage = "";

        Lock redisLock = distributedLockFactory.getDistributedLock("redis");
        redisLock.lock();
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
                testReEntry();
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            redisLock.unlock();
        }
        return retMessage+"\t"+"服务端口号"+port;
    }

    private void testReEntry()//用在V7.0版本程序作为测试可重入性
    {
        Lock redisLock = distributedLockFactory.getDistributedLock("redis");
        redisLock.lock();
        try
        {
            System.out.println("===========测试可重入锁========");
        }finally {
            redisLock.unlock();
        }
    }
*/

    //V6.0 ，不满足可重入性，需要重新修改为V7.0
    /*public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        //redislock();
        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
                testReEnter();
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            //unredislock();
            //改进点，修改为Lua脚本的redis分布式锁调用，必须保证原子性，参考官网脚本案例
            String luaScript =
                    "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del',KEYS[1]) " +
                    "else " +
                        "return 0 " +
                    "end";
            stringRedisTemplate.execute(new DefaultRedisScript(luaScript,Boolean.class), Arrays.asList(key),uuidValue);
        }
        return retMessage+"\t"+"服务端口号"+port;
    }

    private void testReEnter()
    {
       *//* String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        redislock();
        //biz......
        unredislock();
        //改进点，修改为Lua脚本的redis分布式锁调用，必须保证原子性，参考官网脚本案例
        String luaScript =
                "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del',KEYS[1]) " +
                        "else " +
                        "return 0 " +
                        "end";
        stringRedisTemplate.execute(new DefaultRedisScript(luaScript,Boolean.class), Arrays.asList(key),uuidValue);*//*
    }*/


    //V5.0 ,存在问题就是最后的判断+del不是一行原子命令操作，需要用lua脚本进行修改
    /*public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            //改进点，只能删除属于自己的key，不能删除别人的
            // v5.0判断加锁与解锁是不是同一个客户端，同一个才行，自己只能删除自己的锁，不误删他人的
            if(stringRedisTemplate.opsForValue().get(key).equalsIgnoreCase(uuidValue))
            {
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/



    /*
    V4.0,存在问题：stringRedisTemplate.delete(key);只能自己删除自己的锁，不可以删除别人的，需要添加判断
    是否是自己的锁来进行操作
    public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        //改进点：加锁和过期时间设置必须同一行，保证原子性
        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        //stringRedisTemplate.expire(key,30L,TimeUnit.SECONDS);

        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            stringRedisTemplate.delete(key);
        }
        return retMessage+"\t"+"服务端口号"+port;
    }
     */



    /**
     * V3.2，存在的问题
     * 部署了微服务的Java程序机器挂了，代码层面根本没有走到finally这块，
     * 没办法保证解锁(无过期时间该key一直存在)，这个key没有被删除，需要加入一个过期时间限定key
     * @return
     */
    /*public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        //不用递归了，高并发下容易出错，我们用自旋替代递归方法重试调用;也不用if了，用while来替代
        while(!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            stringRedisTemplate.delete(key);
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/


    /*
    V3.1，递归重试，容易导致stackoverflowerror，所以不太推荐；另外，高并发唤醒后推荐用while判断而不是if
    public String sale()
    {
        String retMessage = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue);
        //flag=false,抢不到的线程要继续重试。。。。。。
        if(!flag)
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
            sale();
        }else{
            //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
            try
            {
                //1 查询库存信息
                String result = stringRedisTemplate.opsForValue().get("inventory001");
                //2 判断库存是否足够
                Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
                //3 扣减库存，每次减少一个
                if(inventoryNumber > 0)
                {
                    stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                    retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                    System.out.println(retMessage+"\t"+"服务端口号"+port);
                }else{
                    retMessage = "商品卖完了,o(╥﹏╥)o";
                }
            }finally {
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/

    /*V2.0,单机版加锁配合Nginx和Jmeter压测后，不满足高并发分布式锁的性能要求，出现超卖
    private Lock lock = new ReentrantLock();
    public String sale()
    {
        String retMessage = "";

        lock.lock();
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            lock.unlock();
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/
}
