package com.owofurry.furry.img.service.imp;


import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.lang.func.Supplier2;
import cn.hutool.core.util.RandomUtil;
import com.owofurry.furry.img.dto.VerifyData;
import com.owofurry.furry.img.dto.param.ImageVerifyParam;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.service.ImageCodeVerifyService;
import com.owofurry.furry.img.utils.CodeUtil;
import com.owofurry.furry.img.vo.ImageVerifyResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ImageCodeVerifyServiceImp implements ImageCodeVerifyService {

    private static final List<Supplier2<AbstractCaptcha, Integer, Integer>> captcha = List.of(CaptchaUtil::createCircleCaptcha, CaptchaUtil::createGifCaptcha, CaptchaUtil::createLineCaptcha, CaptchaUtil::createShearCaptcha);
    RedisTemplate<String, Object> redisTemplate;

    public ImageCodeVerifyServiceImp(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ImageVerifyResponse create(ImageVerifyParam codeVerify, int length) {
        if (codeVerify.getHost() != null) {
            Long increment = redisTemplate.opsForValue().increment("verify:req_num:" + codeVerify.getHost());
            redisTemplate.expire("verify:req_num:" + codeVerify.getHost(), 10, TimeUnit.MINUTES);
            assert increment != null;
            if (increment > 20) {
                throw new UserOperationException("请求频繁，请稍后再试");
            }
        }
        // 判断是否达到最大限制
        Long increment = redisTemplate.opsForValue().increment("verify:req_num:" + codeVerify.getTo());
        redisTemplate.expire("verify:req_num:" + codeVerify.getTo(), 10, TimeUnit.MINUTES);
        assert increment != null;
        if (increment > 20) {
            throw new UserOperationException("请求频繁，请稍后再试");
        }
        // 设置验证码原
        RandomGenerator randomGenerator = new RandomGenerator(CodeUtil.CODE_SOURCE, length);
        AbstractCaptcha captcha = randomCaptcha();
        captcha.setGenerator(randomGenerator);
        captcha.createCode();
        String code = captcha.getCode();
        VerifyData verifyData = new VerifyData(ObjectId.next(), codeVerify.getHost(), code, codeVerify.getTo(), codeVerify.getRemark(), false);
        while (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent("verify:code:" + verifyData.getUuid(), verifyData, 6, TimeUnit.MINUTES))) {
            verifyData.setUuid(ObjectId.next());
        }
        return new ImageVerifyResponse(verifyData.getUuid(), captcha.getImageBytes());
    }

    private AbstractCaptcha randomCaptcha() {
        return captcha.get(RandomUtil.randomInt(0, captcha.size())).get(300, 100);
    }
}
