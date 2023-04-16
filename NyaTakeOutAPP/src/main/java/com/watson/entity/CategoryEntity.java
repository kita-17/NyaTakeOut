package com.watson.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class CategoryEntity implements Serializable {
    /**
     * 分类信息主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    Long id;
    /**
     * 给前端用的key与id一致
     */
    @TableField(exist = false)
    Long key;
    /**
     * 分类所属店铺ID
     */
    Long storeId;
    /**
     * 分类名称
     */
    String name;
    /**
     * 分类类型
     * 1 菜品分类 or 2 套餐分类
     */
    Integer type;
    /**
     * 分类排序将决定在小程序端的排序顺序，升序
     */
    Integer sort;
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
