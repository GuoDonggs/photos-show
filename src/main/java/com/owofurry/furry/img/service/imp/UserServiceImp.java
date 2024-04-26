package com.owofurry.furry.img.service.imp;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.owofurry.furry.img.config.RootUserConfiguration;
import com.owofurry.furry.img.entity.User;
import com.owofurry.furry.img.exception.SystemRunningException;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.mapper.UserMapper;
import com.owofurry.furry.img.service.UserService;
import com.owofurry.furry.img.utils.JWTUtil;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.vo.R;
import com.owofurry.furry.img.vo.UserDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private final String PASSWORD_SALT = "你好SB";
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper userMapper;
    RootUserConfiguration rootUserConfiguration;

    public UserServiceImp(RootUserConfiguration rootUserConfiguration,
                          RedisTemplate<String, Object> redisTemplate,
                          UserMapper userMapper) {
        this.rootUserConfiguration = rootUserConfiguration;
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
    }

    @PostConstruct
    private void init() {
        log.info("初始化 root 用户信息");
        // 初始化root用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", "管理员");
        wrapper.eq("user_email", "root");
        wrapper.eq("roles", "root");
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            user = new User();
            user.setUserName("管理员");
            user.setUserEmail("root");
            user.setRoles("root");
            user.setRegisterDate(DateUtil.date());
            user.setUserPassword(SecureUtil.md5(rootUserConfiguration.getPassword() + PASSWORD_SALT));
            userMapper.insert(user);
        } else {
            user.setUserPassword(SecureUtil.md5(rootUserConfiguration.getPassword() + PASSWORD_SALT));
            userMapper.updateById(user);
        }
        log.info("root 用户信息初始化完成，root 密码：{}", rootUserConfiguration.getPassword());
    }

    @Override
    @Transactional
    public R register(String email, String credential) {
        assert email != null && credential != null;
        // 如果用户存在，抛出账户已注册的异常
        if (exist(email)) {
            throw new UserOperationException("账户已注册");
        }
        // 创建一个新的User对象
        User user = new User();
        user.setRegisterDate(DateUtil.date());
        user.setUserEmail(email);
        user.setBanned(false);
        user.setUserPassword(SecureUtil.md5(credential + PASSWORD_SALT));
        log.info("注册新用户：{}", user);
        // 将User对象插入数据库
        userMapper.insert(user);
        User t = userMapper.selectOne(new QueryWrapper<User>().eq("user_email", email));
        if (t == null) {
            log.warn("注册失败，注册完成后数据库中未找到该用户 obj:{}", user);
            throw new SystemRunningException("注册失败");
        }
        // 设置缓存
        redisTemplate.opsForValue().set("user:email:" + email, t);
        redisTemplate.opsForValue().set("user:id:" + t.getUserId(), t);
        // 返回JWT加密后的字符串
        return RUtil.ok(new UserDto(user.getUserId(), user.getUserName(), JWTUtil.encode(email, t.getUserId().toString())));
    }

    @Override
    @Cacheable(cacheNames = "user", key = "#email +':passwd:' +#credential")
    public R login(String email, String credential, String address) {
        User user = getUser(email);
        // 如果用户不存在，抛出账户不存在异常
        if (user == null) {
            throw new UserOperationException("账户不存在");
        }
        if (user.isBanned()) {
            throw new UserOperationException("账户已被封禁");
        }
        if (user.getUserPassword().equals(SecureUtil.md5(credential + PASSWORD_SALT))) {
            return RUtil.ok(new UserDto(user.getUserId(),
                    user.getUserName(),
                    JWTUtil.encode(email, user.getUserId().toString(), address, 7, Calendar.DATE)
            ));
        }
        throw new UserOperationException("账户或密码错误");
    }

    @Override
    @CacheEvict(cacheNames = "user", key = "#email +':' +#credential")
    public R resetPasswd(String email, String credential) {
        User user = getUser(email);
        if (user == null) {
            throw new UserOperationException("账户不存在");
        }
        user.setUserPassword(SecureUtil.md5(credential + PASSWORD_SALT));
        userMapper.updateById(user);
        return RUtil.ok();
    }

    @Override
    public User getUser(String email) {
        // 查找缓存
        User user = (User) redisTemplate.opsForValue().get("user:email:" + email);
        if (user == null) {
            user = userMapper.selectOne(new QueryWrapper<User>().eq("user_email", email));
            // 回写有效缓存或空缓存
            redisTemplate.opsForValue().set("user:email:" + email, user == null ? new User() : user, 30, TimeUnit.MINUTES);
        }
        // 判断是否为空缓存
        if (user != null && user.getUserId() == null) {
            user = null;
        }
        return user;
    }

    @Override
    public User getUser(Integer uid) {
        // 查找缓存
        User user = (User) redisTemplate.opsForValue().get("user:id:" + uid);
        if (user == null) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", uid);
            user = userMapper.selectOne(wrapper);
            user = user == null ? new User() : user;
            // 回写有效缓存或空缓存
            redisTemplate
                    .opsForValue()
                    .set("user:id:" + uid, user, 30, TimeUnit.MINUTES);
        }
        // 判断是否为空缓存
        return user.getUserId() == null ? null : user;
    }

    @Override
    @Cacheable(cacheNames = "user:exist", key = "#email")
    public boolean exist(String email) {
        return getUser(email) != null;
    }

    @Override
    @CacheEvict(cacheNames = "user:id", key = "#userId")
    public void setUsername(String username, Integer userId) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.set("user_name", username);
        userMapper.update(wrapper);
    }
}
