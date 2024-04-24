package com.owofurry.furry.img.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户登录信息")
public class UserLoginParam {
    @NotBlank(message = "邮箱不得为空")
    @Schema(description = "用户邮箱", example = "2454487632@qq.com")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式错误")
    String email;

    @Schema(description = "6-19位密码字母+数字，可选特殊字符：=;*!@#$%^&:<>,./?", example = "112354$%ghvv")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z=;*!@#$%^&:<>,./?]{6,}$", message = "密码格式错误")
    String password;

    @NotBlank
    String verifyCode;
    @NotBlank
    String verifyId;
}
