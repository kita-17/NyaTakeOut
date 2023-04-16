package com.watson.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("dish_flavor")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DishFlavorEntity implements Serializable {
    /**
     * 口味Id主键
     */
    Long id;
    /**
     * 所属菜品id
     */
    Long dishId;
    /**
     * 口味信息值，例如微辣
     */
    String value;
    /**
     * 所属分类？ 例如辣度
     */
    String name;
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
