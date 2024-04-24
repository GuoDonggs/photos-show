package com.owofurry.furry.img.controller.verify;


import com.owofurry.furry.img.dto.param.ImageVerifyParam;
import com.owofurry.furry.img.pojo.VerifyRemark;
import com.owofurry.furry.img.service.ImageCodeVerifyService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.RequestAddressUtil;
import com.owofurry.furry.img.vo.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图片验证码获取
 *
 * @author gs
 * @date 2024/03/14
 */
@RestController
@RequestMapping("/verify")
@Tag(name = "图片验证码", description = "图片验证码相关接口")
public class ImageCodeVerifyController {
    ImageCodeVerifyService verifyService;

    public ImageCodeVerifyController(ImageCodeVerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/image")
    public ResponseEntity<R> getImageCode(@RequestParam(value = "to", required = false) String to,
                                          HttpServletRequest request) {
        ImageVerifyParam param = new ImageVerifyParam();
        if (to == null) {
            param.setTo(RequestAddressUtil.getRemoteAddress(request));
        } else {
            param.setTo(to);
        }
        param.setHost(RequestAddressUtil.getRemoteAddress(request));
        param.setRemark(VerifyRemark.IMAGE_CODE);
        return RUtil.okRE(verifyService.create(param, 6));
    }
}