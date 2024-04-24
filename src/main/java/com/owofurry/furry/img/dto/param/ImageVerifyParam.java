package com.owofurry.furry.img.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;

/**
 * 请求图像验证码的信息体
 *
 * @author gs
 * @date 2024/03/16
 */
@Data
public class ImageVerifyParam {

    @Schema(description = "请求的ip地址/账号id，主要作用为标识用户用于限流", example = "127.0.0.1")
    @Nullable
    String to;

    @Schema(description = "请求的ip地址，如果to为账号ip地址填这里，主要作用也是限流", example = "127.0.0.1")
    @Nullable
    String host;

    @Schema(description = "备注信息")
    @Nullable
    String remark;
}
