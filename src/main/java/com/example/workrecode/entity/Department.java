package com.example.workrecode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 部门实体类
 * 对应表：department
 */
@TableName("department")
public class Department implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 部门名称 */
    private String name;

    /** 部门描述 */
    private String description;

    /**
     * 员工数量（不在 department 表中，是统计字段）
     * 必须标记为非表字段，否则 MyBatis 会当成列去查，导致 Unknown column
     */
    @TableField(exist = false)
    private Integer employeeCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }
}
