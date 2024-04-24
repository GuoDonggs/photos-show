package com.owofurry.furry.img.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片对应关键字的反向记录，便于快速索引
 *
 * @TableName keyword_image
 */
@TableName(value = "keyword_image")
@Data
public class KeywordImage implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     *
     */
    private Long keywordId;
    /**
     * 对应图片id
     */
    private Long fileId;
}