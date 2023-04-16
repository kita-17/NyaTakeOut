package com.watson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.watson.common.JsonDecimalSerialize;
import lombok.Data;

/**
 * 订单明细表
 *
 * @TableName order_detail
 */
@TableName(value = "order_detail")
@Data
public class OrderDetailEntity implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    //商品类型 0 商品 1套餐
    @TableField(exist = false)
    private Integer type;

    /**
     * 名字
     */
    private String name;

    /**
     * 图片
     */
    private String image;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 菜品id
     */
    private Long dishId;

    /**
     * 套餐id
     */
    private Long setmealId;

    /**
     * 口味
     */
    private String dishFlavor;

    /**
     * 数量
     */
    private Integer amount;

    /**
     * 金额
     */
    @JsonSerialize(using = JsonDecimalSerialize.class)
    private BigDecimal price;
}