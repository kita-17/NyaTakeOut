package com.watson.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("setmeal_dish")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetmealDishEntity implements Serializable {
    /**
     * 套餐菜品主键Id
     */
    Long id;
    /**
     * 所属套餐id
     */
    Long setmealId;
    /**
     * 对应菜品Id
     */
    Long dishId;
    @TableField(exist = false)
    String label;
    @TableField(exist = false)
    Long value;
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
