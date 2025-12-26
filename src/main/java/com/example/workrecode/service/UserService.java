package com.example.workrecode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.workrecode.entity.User;

public interface UserService extends IService<User> {
    // 用户登录
    User login(String username, String password);
}