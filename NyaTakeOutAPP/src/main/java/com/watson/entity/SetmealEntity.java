package com.watson.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.watson.common.JsonDecimalSerialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("setmeal")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetmealEntity implements Serializable {
    /**
     * 套餐id，主键
     */
    Long id;
    /**
     * 所属店铺Id
     */
    @TableField("store_id")
    Long storeId;
    /**
     * 对应分类id
     */
    @TableField("category_id")
    Long category;
    /**
     * 套餐名
     */
    String name;
    /**
     * 套餐图片
     */
    String image;
    /**
     * 套餐单价
     */
    @JsonSerialize(using = JsonDecimalSerialize.class)
    BigDecimal price;
    /**
     * 套餐描述
     */
    String description;
    /**
     * 售卖状态
     */
    boolean status;
    /**
     * 销量
     */
    Integer sale;
    Integer type;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
