package com.owofurry.furry.img.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "校验验证码")
public class VerifyCodeParam {

    @Schema(description = "验证码唯一ID", example = "65f2b10ee0fae7e6c5b657e2")
    @NotBlank
    String uuid;

    @Schema(description = "用户输入的验证码", example = "AS03sd")
    @NotBlank
    String code;

    @Schema(description = "用户ip地址", example = "127.0.0.1")
    @Nullable
    String host;

    @Schema(description = "备注信息，用来确认验证类型，防止验证串码")
    @NotBlank
    String remark;
}
