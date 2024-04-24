package com.owofurry.furry.img.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "图片验证码响应体")
public class ImageVerifyResponse {
    @Schema(description = "验证码的唯一id", example = "65f2b10ee0fae7e6c5b657e2")
    String uuid;

    @Schema(description = "图片字节数据", example = "[1,24,413,....]")
    byte[] data;
}
