package com.example.workrecode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.workrecode.entity.Employee;

import java.util.List;

/**
 * 员工Mapper接口
 */
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 继承BaseMapper后，自动获得基本的CRUD方法

    /**
     * 查询所有员工，并关联部门信息
     * @return 员工列表
     */
    List<Employee> selectAllWithDepartment();

    /**
     * 根据部门ID查询员工
     * @param departmentId 部门ID
     * @return 员工列表
     */
    List<Employee> selectByDepartmentId(Long departmentId);
}
