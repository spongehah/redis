package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.SimpleRedisLock;
import com.hmdp.utils.UserHolder;
import io.lettuce.core.XReadArgs;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.hmdp.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    
    @Resource
    private ISeckillVoucherService seckillVoucherService;
    
    @Resource
    private RedisIdWorker redisIdWorker;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Resource
    private RedissonClient redissonClient;
    
    public static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }
    
    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);
    //异步处理线程池
    public static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();
    //在类初始化之后执行，因为当这个类初始化好了之后，随时都是有可能要执行的
    @PostConstruct
    private void init(){
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

    private IVoucherOrderService proxy;
    
    // 用于线程池处理的任务
    // 当初始化完毕后，就会去从对列中去拿信息
    private class VoucherOrderHandler implements Runnable {
        
        //使用stream消息队列
        @Override
        public void run() {
            while (true) {
                try {
                    // 1.获取消息队列中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 >
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create("stream.orders", ReadOffset.lastConsumed())
                    );
                    // 2.判断订单信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有消息，继续下一次循环
                        continue;
                    }
                    // 解析数据
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 3.创建订单
                    proxy.handleVoucherOrder(voucherOrder);
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    //处理异常消息
                    while (true) {
                        try {
                            // 1.获取pending-list中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 0
                            List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                                    Consumer.from("g1", "c1"),
                                    StreamReadOptions.empty().count(1),
                                    StreamOffset.create("stream.orders", ReadOffset.from("0"))
                            );
                            // 2.判断订单信息是否为空
                            if (list == null || list.isEmpty()) {
                                // 如果为null，说明没有异常消息，结束循环
                                break;
                            }
                            // 解析数据
                            MapRecord<String, Object, Object> record = list.get(0);
                            Map<Object, Object> value = record.getValue();
                            VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                            // 3.创建订单
                            proxy.handleVoucherOrder(voucherOrder);
                            // 4.确认消息 XACK
                            stringRedisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
                        } catch (Exception e2) {
                            log.error("处理pendding订单异常", e2);
                            try{
                                Thread.sleep(20);
                            }catch(Exception e3){
                                e3.printStackTrace();
                            }
                        }
                    }
                }
            }
        }


        //使用阻塞队列
       /* @Override
        public void run() {
            while (true) {
                try {
                    // 1.获取队列中的订单信息
                    VoucherOrder voucherOrder = orderTasks.take();
                    // 2.创建订单
                    proxy.handleVoucherOrder(voucherOrder);
                } catch (InterruptedException e) {
                    log.error("处理订单异常", e);
                }
            }
        }*/
    }
    
    @Transactional
    public void handleVoucherOrder(VoucherOrder voucherOrder) {
        // 扣减库存
        seckillVoucherService.update()
                .setSql("stock = stock - 1") // set stock = stock - 1
                .eq("voucher_id", voucherOrder.getVoucherId()).gt("stock", 0) // where id = ? and stock > 0
                .update();
        // 保存订单
        this.save(voucherOrder);
    }


    @Override
    public Result secKillVoucher(Long voucherId) {
        //1 查询优惠券
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        //2 判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀尚未开始！");
        }
        //3 判断秒杀是否结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀已经结束！");
        }
        //4 判断库存是否充足
        if (voucher.getStock() < 1) {
            return Result.fail("库存不足！");
        }

        Long userId = UserHolder.getUser().getId();
        /*synchronized (userId.toString().intern()) {
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            //需要使用代理来调用方法，否则会造成事务失效（这里是造成Spring事务失效的场景之一）
            return proxy.createVoucherOrder(voucherId);
        }*/

        return createVoucherOrder(voucherId);
        
        /**
         * 集群模式下使用分布式锁
         */
        // 创建锁对象
        // SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);

        /*RLock lock = redissonClient.getLock("order" + userId);

        // 获取锁
        // boolean isLock = lock.tryLock(10L);
        boolean isLock = lock.tryLock();

        // 判断锁是否获取成功
        if (!isLock) {
            // 失败，返回错误信息
            return Result.fail("不允许重复下单！");
        }
        
        // 成功，执行业务
        try {
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            //需要使用代理来调用方法，否则会造成事务失效（这里是造成Spring事务失效的场景之一）
            return proxy.createVoucherOrder(voucherId);
        } finally {
            // 释放锁
            lock.unlock();
        }*/
    }

    /**
     * 优化秒杀之后，使用redis且异步执行
     * 也可以直接将此方法当成secKillVoucher方法，但是得补充完成优惠券可购买时间
     */
    @Override
    public Result createVoucherOrder(Long voucherId) {
        // 判断redis中是否一已经缓存了改优惠券的库存信息，若没有，则获取乐观锁进行缓存构建
        String s = stringRedisTemplate.opsForValue().get(SECKILL_STOCK_KEY + voucherId);
        do {
            if (StrUtil.isBlank(s)) {
                RLock lock = redissonClient.getLock("voucher" + voucherId);
                if (lock.tryLock() && StrUtil.isBlank(stringRedisTemplate.opsForValue().get(SECKILL_STOCK_KEY + voucherId))) {
                    try {
                        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
                        Integer stock = voucher.getStock();
                        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucherId, stock.toString());
                        s = stock.toString();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }while (StrUtil.isBlank(s));


        //获取用户
        Long userId = UserHolder.getUser().getId();
        Long orderId = redisIdWorker.nextId("order");
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), orderId.toString()
        );
        int r = result.intValue();
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }
        
        //改为使用Stream消息队列处理
        // //TODO 保存阻塞队列
        // VoucherOrder voucherOrder = new VoucherOrder();
        // // 2.3.订单id
        // voucherOrder.setId(orderId);
        // // 2.4.用户id
        // voucherOrder.setUserId(userId);
        // // 2.5.代金券id
        // voucherOrder.setVoucherId(voucherId);
        // // 2.6.放入阻塞队列
        // orderTasks.add(voucherOrder);

        // 获取代理对象，用于异步下单时处理事务
        proxy =  (IVoucherOrderService)AopContext.currentProxy();
        
        // 3.返回订单id
        return Result.ok(orderId);
    }

    /**
     * 未优化秒杀之前，纯使用mysql串行
     */
    /*@Transactional//扣减库存和创建订单需要保证原子性
    public Result createVoucherOrder(Long voucherId) {
        //5 一人一单
        Long userId = UserHolder.getUser().getId();
        //5.1 查询订单
        int count = this.query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        //5.2 判断是否存在
        if (count > 0) {
            //用户已经购买过 
            return Result.fail("您已经购买过此优惠券！");
        }

        //6 扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherId).gt("stock",0)//采用乐观锁的方式，若库存大于0，才下单
                .update();
        if (!success) {
            return Result.fail("库存不足！");
        }

        //7 创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        //7.1 订单id
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        //7.2 用户id
        voucherOrder.setUserId(userId);
        //7.3 代金券id
        voucherOrder.setVoucherId(voucherId);
        boolean save = this.save(voucherOrder);
        if (save) {
            return Result.ok(orderId);
        }else {
            return Result.fail("下单失败！");
        }
    }*/
}
