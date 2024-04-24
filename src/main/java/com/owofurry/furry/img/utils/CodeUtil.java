package com.owofurry.furry.img.utils;

import cn.hutool.core.util.RandomUtil;

public class CodeUtil {
    public static final String CODE_SOURCE = "qqw990KLtyuiWERYUbnm1IOooZXCxcvrp234QPASFGweasdfghjklz8HJ567VBNM";


    public static String getRandomCode(int len) {
        char[] arr = new char[len];
        for (int i = 0; i < len; i++) {
            arr[i] = CODE_SOURCE.charAt(RandomUtil.randomInt(0, CODE_SOURCE.length()));
        }
        return new String(arr);
    }
}
