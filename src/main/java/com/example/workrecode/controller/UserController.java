package com.example.workrecode.controller;

import com.example.workrecode.common.Result;
import com.example.workrecode.entity.User;
import com.example.workrecode.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的API请求
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginData 包含用户名和密码的请求体
     * @return 登录结果，包含用户信息（不包含密码）
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result loginJson(@RequestBody Map<String, String> loginData) {
        return doLogin(loginData);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Result loginForm(@RequestParam(value = "username", required = false) String username,
                            @RequestParam(value = "password", required = false) String password) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);
        return doLogin(loginData);
    }

    private Result doLogin(Map<String, String> loginData) {
        // 参数验证
        if (loginData == null || loginData.get("username") == null || loginData.get("password") == null) {
            return Result.error("请求参数不完整，请提供用户名和密码");
        }
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        // 参数不能为空验证
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return Result.error("用户名或密码不能为空");
        }
        
        try {
            // 调用登录方法
            User user = userService.login(username, password);
            
            if (user != null) {
                // 登录成功，返回用户信息（不包含密码）
                user.setPassword(null);
                return Result.success("登录成功", user);
            } else {
                // 登录失败
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            // 捕获异常，记录日志
            e.printStackTrace();
            return Result.error("登录过程中发生异常，请稍后重试");
        }
    }
}
