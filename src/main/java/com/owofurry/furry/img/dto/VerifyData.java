package com.owofurry.furry.img.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于保存生成的验证数据到 redis
 *
 * @author gs
 * @date 2024/03/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyData {

    String uuid;
    String host;
    String code;
    // 请求验证码的接受者，手机号、验证码，图片验证码时为 host/null
    String to;
    // 对于验证信息的备注
    String remark;
    // 是否已经通过验证
    boolean verified;
}
