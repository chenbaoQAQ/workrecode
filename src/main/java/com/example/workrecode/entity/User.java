package com.example.workrecode.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String role; // 角色：admin, employee
    private String name;
}