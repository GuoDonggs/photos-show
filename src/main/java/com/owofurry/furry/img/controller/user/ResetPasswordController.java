package com.owofurry.furry.img.controller.user;

import com.owofurry.furry.img.dto.param.UserLoginParam;
import com.owofurry.furry.img.dto.param.VerifyCodeParam;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.pojo.VerifyRemark;
import com.owofurry.furry.img.service.CheckVerifyService;
import com.owofurry.furry.img.service.UserService;
import com.owofurry.furry.img.utils.BindingUtil;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.RequestAddressUtil;
import com.owofurry.furry.img.utils.UserUtil;
import com.owofurry.furry.img.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class ResetPasswordController {

    CheckVerifyService checkVerifyService;

    UserService userService;

    public ResetPasswordController(CheckVerifyService checkVerifyService,
                                   UserService userService) {
        this.checkVerifyService = checkVerifyService;
        this.userService = userService;
    }

    @PostMapping("/reset-password")
    public R resetPassword(@RequestBody @Validated UserLoginParam param,
                           BindingResult bindingResult, HttpServletRequest request) {
        BindingUtil.validate(bindingResult);
        boolean r = checkVerifyService.verify(new VerifyCodeParam(param.getVerifyId(), param.getVerifyCode(),
                RequestAddressUtil.getRemoteAddress(request), VerifyRemark.EMAIL_CODE));
        if (!r) {
            throw new UserOperationException("验证码错误");
        }
        return userService.resetPasswd(param.getEmail(), param.getPassword());
    }

    @PostMapping("/set-username")
    public R setUsername(@RequestParam("username") String username) {
        if (username.length() > 8) {
            throw new UserOperationException("用户名长度不符合要求");
        }
        userService.setUsername(username, UserUtil.getId());
        return RUtil.ok();
    }
}
