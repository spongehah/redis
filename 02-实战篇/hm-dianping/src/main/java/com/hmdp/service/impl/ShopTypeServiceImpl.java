package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryList() {
        String key = RedisConstants.CACHE_TYPE_KEY;
        List<ShopType> typeList = stringRedisTemplate.opsForList().range(key, 0, -1)
                .stream().map(type -> JSONUtil.toBean(type, ShopType.class)).collect(Collectors.toList());
        if (!typeList.isEmpty()) {
            return Result.ok(typeList);
        }
        
        //redis中不存在
        List<ShopType> list = this.query().orderByAsc("sort").list();
        if (list.isEmpty()) {
            return Result.fail("没有商户类别！");
        }
        List<String> listJson = list.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().leftPushAll(key,listJson);
        return Result.ok(list);
    }
}
