package com.watson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Data;

/**
 * 优惠券表
 *
 * @TableName coupon
 */
@TableName(value = "coupon")
@Data
public class CouponEntity implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 关联店铺Id
     */
    @TableField(value = "storeId")
    private Long storeId;

    /**
     * 优惠券名字
     */
    private String name;

    /**
     * 优惠券类型。0 普通券 | 1 限购券
     */
    private Integer type;

    /**
     * 优惠券使用规则（优惠券描述）
     */
    private String rule;

    /**
     * 优惠后金额(20减10中的10)
     */
    private Integer discount;

    /**
     * 使用条件(20减10中的20)
     */
    private Integer couponCondition;

    /**
     * 优惠券库存
     */
    private Integer count;

    /**
     * 优惠券开始售卖的时间
     */
    private LocalDateTime startDate;
    /**
     * 优惠券售卖结束的时间
     */
    private LocalDateTime endDate;

    /**
     * 有效期
     */
    @TableField(value = "valid_days")
    private Integer validDays;

    /**
     * 这个是给用户列表显示优惠券到期时间用的
     */
    @TableField(exist = false)
    private LocalDateTime date;

    @TableField(exist = false)
    private String storeName;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponEntity coupon = (CouponEntity) o;
        return (!id.equals(coupon.id));
    }

    @Override
    public int hashCode() {
        return 31 * id.hashCode() + 10;
    }
}