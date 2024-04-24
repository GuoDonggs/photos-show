package com.owofurry.furry.img.service.imp;

import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.thread.ThreadUtil;
import com.owofurry.furry.img.dto.VerifyData;
import com.owofurry.furry.img.dto.param.EmailVerifyParam;
import com.owofurry.furry.img.dto.param.VerifyCodeParam;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.service.CheckVerifyService;
import com.owofurry.furry.img.service.EmailCodeVerifyService;
import com.owofurry.furry.img.utils.CodeUtil;
import com.owofurry.furry.img.utils.MailUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class EmailCodeVerifyVerifyServiceImp implements EmailCodeVerifyService, CheckVerifyService {
    private static final String DECREASE_RECORD_LUA = "if tonumber(redis.call('get', KEYS[1])) > 0 then redis.call('decr', KEYS[1]) else redis.call('del', KEYS[1]) end";
    private static final int ACCOUNT_MAX_SEND = 5;
    private static final int BAN_MINUTE_TIME = 5;
    RedisTemplate<String, Object> template;
    @Getter
    @Setter
    private int codeLength = 6;

    public EmailCodeVerifyVerifyServiceImp(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    @Override
    public String send(EmailVerifyParam verify) {
        ValueOperations<String, Object> opsForValue = template.opsForValue();
        if (verify.getHost() != null && !verify.getHost().equals("127.0.0.1")) {
            Long increment = opsForValue.increment("verify:req_num:" + verify.getHost());
            assert increment != null;
            if (increment > 20) {
                throw new UserOperationException("请求频繁，请稍后再试");
            }
        }
        // 判断用户获取验证码是否到达上限被 ben
        String to = verify.getTo();
        Long count = opsForValue.increment("verify:req_num:" + to);
        assert count != null;
        template.expire("verify:req_num:" + to, BAN_MINUTE_TIME, TimeUnit.MINUTES);
        if (count > ACCOUNT_MAX_SEND) {
            throw new UserOperationException("请求频繁，稍后再试");
        }
        // 生成验证码
        String randomCode = CodeUtil.getRandomCode(6);
        String uid = ObjectId.next();
        ThreadUtil.execute(() -> {
            // 发送邮件
            MailUtil.sendDefaultHtml("验证码", randomCode, "验证码5分种内有效", to);
        });
        // 放入缓存
        VerifyData verifyData = new VerifyData(uid, verify.getHost(), randomCode, to, verify.getRemark(), false);
        opsForValue.set("verify:code:" + uid, verifyData, 6, TimeUnit.MINUTES);
        return uid;
    }


    @Override
    public boolean verify(VerifyCodeParam codeVerify) {
        if (check(codeVerify.getUuid())) {
            return true;
        }
        ValueOperations<String, Object> opsForValue = template.opsForValue();
        VerifyData data = (VerifyData) opsForValue.get("verify:code:" + codeVerify.getUuid());
        boolean res = data != null && compare(codeVerify, data);
        if (res) {
            data.setVerified(true);
            // 设置缓存为验证成功
            opsForValue.set("verify:code:" + codeVerify.getUuid(), data,
                    2, TimeUnit.MINUTES);
            // 验证成功减少记录的请求次数
            template.execute(new DefaultRedisScript<>(DECREASE_RECORD_LUA), Collections.singletonList("verify:req_num:" + data.getTo()));
        }
        return res;
    }

    public boolean check(String verifyId) {
        VerifyData data = (VerifyData)
                template.opsForValue()
                        .get("verify:code:" + verifyId);
        if (data == null) {
            throw new UserOperationException("验证码已过期");
        }
        return data.isVerified();
    }

    private boolean compare(VerifyCodeParam codeVerify, VerifyData data) {
        if (data.getHost() != null && !data.getHost().equals(codeVerify.getHost())) {
            return false;
        }
        if (data.getRemark() != null && !data.getRemark().equals(codeVerify.getRemark())) {
            return false;
        }
        return codeVerify.getCode().equalsIgnoreCase(data.getCode());
    }
}
