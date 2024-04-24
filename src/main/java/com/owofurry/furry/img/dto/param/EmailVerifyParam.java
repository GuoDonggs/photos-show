package com.owofurry.furry.img.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "接受邮箱验证请求的类")
public class EmailVerifyParam {

    @Schema(description = "接受验证码的邮箱", example = "2818324149@qq.com")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
    String to;

    @Schema(description = "用户ip地址", example = "127.0.0.1")
    @Nullable
    String host;

    @Schema(description = "备注信息")
    @Nullable
    String remark;
}
