package com.owofurry.furry.img.service;


import com.owofurry.furry.img.entity.User;
import com.owofurry.furry.img.vo.R;

public interface UserService {

    R register(String email, String credential);

    R login(String email, String credential);

    R resetPasswd(String email, String credential);

    User getUser(String email);

    User getUser(Integer uid);

    boolean exist(String email);

    void setUsername(String username, Integer userId);

}
