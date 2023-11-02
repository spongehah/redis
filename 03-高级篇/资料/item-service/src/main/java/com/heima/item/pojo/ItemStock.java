package com.heima.item.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_item_stock")
public class ItemStock {
    @TableId(type = IdType.INPUT, value = "item_id")
    private Long id; //商品id
    private Integer stock; //商品库存
    private Integer sold; //商品销量
}
