package com.watson.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 用户收藏的店铺
 *
 * @TableName StoreFavoriteDTO
 */
@TableName(value = "user_store_favorite")
@Data
public class StoreFavoriteDTO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 收藏的店铺Id
     */
    @TableField(value = "storeId")
    private Long storeId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}