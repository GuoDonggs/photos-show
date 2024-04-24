package com.owofurry.furry.img.service;


import com.owofurry.furry.img.dto.param.EmailVerifyParam;

/**
 * 电子邮件验证码
 *
 * @author gs
 * @date 2024/03/13
 */
public interface EmailCodeVerifyService {
    /**
     * 发送
     *
     * @param codeSender 代码发件人
     * @return {@link String}
     */
    String send(EmailVerifyParam codeSender);
}
