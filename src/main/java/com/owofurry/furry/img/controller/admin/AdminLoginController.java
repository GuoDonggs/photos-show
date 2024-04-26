package com.owofurry.furry.img.controller.admin;

import com.owofurry.furry.img.dto.param.RootLoginParam;
import com.owofurry.furry.img.service.UserService;
import com.owofurry.furry.img.utils.BindingUtil;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.RequestAddressUtil;
import com.owofurry.furry.img.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminLoginController {

    UserService userService;

    public AdminLoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/login")
    public R login(@RequestBody @Validated RootLoginParam param,
                   BindingResult result, HttpServletRequest request) {
        BindingUtil.validate(result);
        return RUtil.ok(userService.login(param.getUsername(), param.getPasswd(), RequestAddressUtil.getRemoteAddress(request)));
    }
}