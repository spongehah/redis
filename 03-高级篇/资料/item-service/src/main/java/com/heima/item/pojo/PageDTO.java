package com.heima.item.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PageDTO {
    private Long total;
    private List<Item> list;

    public PageDTO() {
    }

    public PageDTO(Long total, List<Item> list) {
        this.total = total;
        this.list = list;
    }
}
