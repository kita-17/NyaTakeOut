package com.watson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 评论关联的图片
 * @TableName comment_image
 */
@TableName(value ="comment_image")
@Data
public class CommentImageEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 该图片对应的评论Id
     */
    private Long commentId;

    /**
     * 图片文件名
     */
    private String fileName;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}