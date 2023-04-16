package com.watson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * 用户拥有的优惠券表
 *
 * @TableName user_coupon
 */
@TableName(value = "user_coupon")
@Data
public class UserCouponEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 优惠券id
     */
    @TableField(value = "couponId")
    private Long couponId;

    /**
     * 是否已使用 0 否 | 1是
     */
    private Integer status;

    /**
     * 优惠券到期时间
     */
    private LocalDateTime date;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}