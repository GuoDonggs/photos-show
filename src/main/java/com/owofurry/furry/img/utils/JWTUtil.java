package com.owofurry.furry.img.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.owofurry.furry.img.config.security.jwt.JWTAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

public class JWTUtil {
    private static final JWTSigner jwtSigner = JWTSignerUtil.hs384("qa0suh0qsnd0qns0hx}+-2w".getBytes(StandardCharsets.UTF_8));


    public static String encode(String principal, String credential) {
        return JWT.create()
                .setPayload("credential", credential)
                .setPayload("principal", principal)
                .sign(jwtSigner);
    }

    public static String encode(String principal, String credential, String host, int expireTime, int calendarUnit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(calendarUnit, expireTime);
        return JWT.create()
                .setExpiresAt(calendar.getTime())
                .setPayload("credential", credential)
                .setPayload("principal", principal)
                .setPayload("host", host)
                .setPayload("expireTime", calendar.getTime())
                .sign(jwtSigner);
    }

    public static JWTAuthenticationToken decode(String jwt) {
        JWT j = JWT.of(jwt);
        return new JWTAuthenticationToken(
                String.valueOf(j.getPayload("principal")),
                String.valueOf(j.getPayload("credential")),
                String.valueOf(j.getPayload("host"))
        );
    }

    public static boolean verify(String jwt) {
        try {
            JWTValidator.of(jwt).validateAlgorithm(jwtSigner).validateDate();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
