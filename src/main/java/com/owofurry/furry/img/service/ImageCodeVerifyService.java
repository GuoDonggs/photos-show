package com.owofurry.furry.img.service;

import com.owofurry.furry.img.dto.param.ImageVerifyParam;
import com.owofurry.furry.img.vo.ImageVerifyResponse;

public interface ImageCodeVerifyService {
    ImageVerifyResponse create(ImageVerifyParam codeVerify, int length);
}
