package com.owofurry.furry.img.service;


import com.owofurry.furry.img.dto.param.VerifyCodeParam;

public interface CheckVerifyService {
    boolean check(String verifyId);

    /**
     * 验证
     *
     * @param codeVerify 代码验证
     * @return boolean
     */
    boolean verify(VerifyCodeParam codeVerify);
}
