package com.watson.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.watson.common.JsonDecimalSerialize;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dish")
public class DishEntity implements Serializable {
    /**
     * 菜品Id主键，雪花算法生成
     */
    Long id;
    /**
     * 菜品所属店铺Id
     */
    Long storeId;
    /**
     * 菜品名称
     */
    String name;
    /**
     * 菜品图片文件名称
     */
    String image;
    /**
     * 所属分类id
     */
    @TableField("category_id")
    Long category;
    /**
     * 单价
     */
    @JsonSerialize(using = JsonDecimalSerialize.class)
    BigDecimal price;
    /**
     * 售卖状态
     */
    boolean status;
    /**
     * 菜品描述
     */
    String description;
    /**
     * 销量
     */
    Integer sale;
    //TODO what is？
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

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
