package com.imooc.mybatis.dto;

import com.imooc.mybatis.entity.Category;
import com.imooc.mybatis.entity.Goods;
//Data Transfer Object -- 数据传输对象
public class GoodsDTO {
    private Goods goods;// = new Goods();
//    private String categoryName;
    private Category category;// = new Category();
    private String test;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
