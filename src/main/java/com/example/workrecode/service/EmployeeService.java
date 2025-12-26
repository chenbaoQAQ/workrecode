package com.example.workrecode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.workrecode.entity.Employee;

import java.util.List;

/**
 * 员工Service接口
 */
public interface EmployeeService extends IService<Employee> {
    // 继承IService后，自动获得基本的CRUD方法

    /**
     * 查询所有员工，并关联部门信息
     * @return 员工列表
     */
    List<Employee> findAllWithDepartment();

    /**
     * 根据ID查询员工
     * @param id 员工ID
     * @return 员工信息
     */
    Employee findById(Long id);

    /**
     * 根据部门ID查询员工
     * @param departmentId 部门ID
     * @return 员工列表
     */
    List<Employee> findByDepartmentId(Long departmentId);

    /**
     * 保存员工
     * @param employee 员工信息
     * @return 保存后的员工
     */
    Employee saveEmployee(Employee employee);

    /**
     * 更新员工
     * @param employee 员工信息
     * @return 更新后的员工
     */
    Employee updateEmployee(Employee employee);

    /**
     * 删除员工
     * @param id 员工ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
}
