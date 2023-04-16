package com.watson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 商店表
 * @TableName StoreEntity
 */
@TableName(value ="store")
@Data
public class StoreEntity implements Serializable {
    /**
     * 商店id
     */
    @TableId
    private Long id;

    /**
     * 商店名称
     */
    private String title;

    /**
     * 商店所有者Id, 关联Employee的ID
     */
    @TableField(value = "ownerId")
    private Long ownerId;

    /**
     * 商店图标地址
     */
    private String icon;

    /**
     * 商店描述
     */
    private String description;

    /**
     * 商店状态
     */
    private String status;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}