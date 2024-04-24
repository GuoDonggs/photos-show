package com.owofurry.furry.img.controller.user;

import com.owofurry.furry.img.dto.param.UserLoginParam;
import com.owofurry.furry.img.dto.param.VerifyCodeParam;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.pojo.VerifyRemark;
import com.owofurry.furry.img.service.CheckVerifyService;
import com.owofurry.furry.img.service.UserService;
import com.owofurry.furry.img.utils.BindingUtil;
import com.owofurry.furry.img.utils.RequestAddressUtil;
import com.owofurry.furry.img.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class RegisterController {

    UserService userService;
    CheckVerifyService checkVerifyService;

    public RegisterController(UserService userService,
                              CheckVerifyService checkVerifyService) {
        this.userService = userService;
        this.checkVerifyService = checkVerifyService;
    }

    @PostMapping("/register")
    public R register(@RequestBody UserLoginParam param, BindingResult bindingResult,
                      HttpServletRequest request) {
        BindingUtil.validate(bindingResult);
        boolean r = checkVerifyService.verify(new VerifyCodeParam(param.getVerifyId(), param.getVerifyCode(),
                RequestAddressUtil.getRemoteAddress(request), VerifyRemark.EMAIL_CODE));
        if (!r) {
            throw new UserOperationException("验证码错误");
        }
        return userService.register(param.getEmail(), param.getPassword());
    }
}
