package com.owofurry.furry.img.controller.verify;


import com.owofurry.furry.img.dto.param.EmailVerifyParam;
import com.owofurry.furry.img.pojo.VerifyRemark;
import com.owofurry.furry.img.service.EmailCodeVerifyService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.RequestAddressUtil;
import com.owofurry.furry.img.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify")
@Tag(name = "邮箱验证码", description = "邮箱验证码相关接口")
public class MailCodeVerifyController {

    EmailCodeVerifyService verifyService;


    public MailCodeVerifyController(EmailCodeVerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/mail")
    @Operation(summary = "发送邮箱验证码")
    @Parameter(name = "to", description = "邮箱地址", required = true, example = "12345678@qq.com")
    public ResponseEntity<R> sendCode(@RequestParam("to") String to,
                                      HttpServletRequest request) {
        EmailVerifyParam param = new EmailVerifyParam();
        param.setTo(to);
        param.setHost(RequestAddressUtil.getRemoteAddress(request));
        param.setRemark(VerifyRemark.EMAIL_CODE);
        return RUtil.okRE(verifyService.send(param));
    }
}
