package com.owofurry.furry.img.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;
    /**
     *
     */
    private String userName;
    /**
     *
     */
    private String userPassword;
    /**
     *
     */
    private String userEmail;
    /**
     *
     */
    private Date registerDate;

    private boolean banned;

    private String roles;

    public String[] roles2Array() {
        return roles.split(",");
    }
}