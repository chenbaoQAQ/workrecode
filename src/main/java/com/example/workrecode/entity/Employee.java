package com.example.workrecode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 员工实体类
 * 注意：employee 表只有 department_id，没有 department_name
 * department_name 是在 EmployeeMapper.xml 里 join department 查出来的
 * 所以这里用 exist=false 接收 join 的字段，用于前端展示
 */
@Data
@TableName("employee")
public class Employee {

    /** 员工ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 员工姓名 */
    private String name;

    /** 员工性别 */
    private String gender;

    /** 部门ID（对应表字段 department_id，开启了下划线转驼峰） */
    private Long departmentId;

    /** join 查询出来的部门名称（列名 department_name -> 驼峰 departmentName） */
    @TableField(exist = false)
    private String departmentName;

    /** join 查询出来的部门描述（列名 department_description -> 驼峰 departmentDescription） */
    @TableField(exist = false)
    private String departmentDescription;
}
