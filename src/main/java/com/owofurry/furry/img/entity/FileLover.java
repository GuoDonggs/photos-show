package com.owofurry.furry.img.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @TableName file_lover
 */
@TableName(value = "file_lover")
@Data
public class FileLover implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private Integer userId;
    /**
     *
     */
    private Long fileId;
}