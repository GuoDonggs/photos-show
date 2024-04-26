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
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class LoginController {

    UserService service;

    CheckVerifyService checkVerifyService;

    public LoginController(UserService service, CheckVerifyService checkVerifyService) {
        this.service = service;
        this.checkVerifyService = checkVerifyService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public R login(@RequestBody @Validated UserLoginParam login, BindingResult result,
                   HttpServletRequest request) {
        BindingUtil.validate(result);
        boolean r = checkVerifyService.verify(new VerifyCodeParam(login.getVerifyId(),
                login.getVerifyCode(),
                RequestAddressUtil.getRemoteAddress(request),
                VerifyRemark.IMAGE_CODE));
        if (!r) {
            throw new UserOperationException("验证码错误");
        }
        return service.login(login.getEmail(), login.getPassword(), RequestAddressUtil.getRemoteAddress(request));
    }

}