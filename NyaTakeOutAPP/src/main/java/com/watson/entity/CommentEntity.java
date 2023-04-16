package com.watson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import lombok.Data;

/**
 * 评论表
 *
 * @TableName comment
 */
@TableName(value = "comment")
@Data
public class CommentEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     *
     */
    @TableField(value = "store_id")
    private Long storeId;

    @TableField(value = "order_id")
    private Long orderId;
    /**
     *
     */
    private String content;

    private Integer likes;
    /**
     *
     */
    private LocalDate createDate;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}