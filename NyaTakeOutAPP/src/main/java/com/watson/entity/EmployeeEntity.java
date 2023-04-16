package com.watson.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 员工信息
 *
 * @TableName employee
 */
@Data
@TableName("employee")
public class EmployeeEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 身份证
     */
    @TableField(value = "id_number")
    private Long idNumber;
    /**
     * 前端使用的key
     */
    @TableField(exist = false)
    private Long key;

    /**
     * 姓名
     */
    private String name;
    /**
     * 员工所属店铺Id
     */
    @TableField(value = "storeId")
    private Long storeId;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private String sex;


    /**
     * 状态 0:禁用，1:正常
     */
    private boolean status;
    /**
     * 权限
     */
    private Integer permissions;

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