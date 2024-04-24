package com.owofurry.furry.img.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName keyword
 */
@TableName(value = "keyword")
@Data
public class Keyword implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 存储已有关键词
     */
    @TableId
    private Long id;

    private String keyword;
}