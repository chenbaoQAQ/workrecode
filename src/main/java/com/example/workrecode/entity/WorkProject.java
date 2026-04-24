package com.example.workrecode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("work_project")
public class WorkProject {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
