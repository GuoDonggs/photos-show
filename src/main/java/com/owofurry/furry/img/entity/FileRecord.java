package com.owofurry.furry.img.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName image_record
 */
@TableName(value = "file_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRecord implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 图像的所属id
     */
    @TableId(type = IdType.AUTO)
    private Long FileId;
    /**
     * 上传的用户id
     */
    private Integer uploadUser;
    /**
     * 上传的时间
     */
    private Date uploadDate;

    private String title;

    private String introduce;

    private Long loverNum;

    /**
     * 图片的标签
     */
    private String keywords;
    /**
     * 是否已经确认图片无违规，已确认 1 未确认 0
     */
    private Boolean hasChecked;

    private String filePath;
}