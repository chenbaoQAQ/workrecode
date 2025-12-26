package com.example.workrecode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.workrecode.entity.User;
import com.example.workrecode.mapper.UserMapper;
import com.example.workrecode.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        // 暂时使用明文密码验证，确保系统能正常运行
        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
