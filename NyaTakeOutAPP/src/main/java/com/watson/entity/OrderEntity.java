package com.watson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.watson.common.JsonDecimalSerialize;
import lombok.Data;

/**
 * 订单表
 *
 * @TableName orders
 */
@TableName(value = "orders")
@Data
public class OrderEntity implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 订单对应店铺Id
     */
    @TableField(value = "store_id")
    Long storeId;

    @TableField(exist = false)
    private String storeName;
    /**
     * 订单号
     */
    private String number;

    /**
     * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
     */
    private Integer status;

    /**
     * 下单用户
     */
    private Long userId;

    /**
     * 地址id
     */
    private Long addressBookId;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 结账时间
     */
    private LocalDateTime checkoutTime;

    /**
     * 支付方式 1微信,2支付宝
     */
    private Integer payMethod;

    /**
     * 使用的优惠券Id
     */
    @TableField(value = "couponId")
    private Long couponId;

    /**
     * 实收金额
     */
    @JsonSerialize(using = JsonDecimalSerialize.class)
    private BigDecimal price;

    /**
     * 备注
     */
    private String remark;
}