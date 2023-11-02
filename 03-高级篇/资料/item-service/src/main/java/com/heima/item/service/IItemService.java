package com.heima.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.item.pojo.Item;

public interface IItemService extends IService<Item> {
    void saveItem(Item item);
}
