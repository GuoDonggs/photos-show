package com.owofurry.furry.img.controller.user;

import com.owofurry.furry.img.entity.User;
import com.owofurry.furry.img.service.UserService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.UserUtil;
import com.owofurry.furry.img.vo.R;
import com.owofurry.furry.img.vo.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class CheckController {

    UserService userService;

    public CheckController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check")
    public R check() {
        User user = userService.getUser(UserUtil.getId());
        if (user == null) {
            return RUtil.notFound();
        }
        return RUtil.ok(new UserDto(user.getUserId(), user.getUserName()));
    }


}
