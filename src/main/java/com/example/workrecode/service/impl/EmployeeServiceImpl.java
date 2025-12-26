package com.example.workrecode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.workrecode.entity.Employee;
import com.example.workrecode.mapper.EmployeeMapper;
import com.example.workrecode.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 员工Service实现类
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 查询所有员工，并关联部门信息
     * @return 员工列表
     */
    @Override
    public List<Employee> findAllWithDepartment() {
        return employeeMapper.selectAllWithDepartment();
    }

    /**
     * 根据ID查询员工
     * @param id 员工ID
     * @return 员工信息
     */
    @Override
    public Employee findById(Long id) {
        return employeeMapper.selectById(id);
    }

    /**
     * 根据部门ID查询员工
     * @param departmentId 部门ID
     * @return 员工列表
     */
    @Override
    public List<Employee> findByDepartmentId(Long departmentId) {
        return employeeMapper.selectByDepartmentId(departmentId);
    }

    /**
     * 保存员工
     * @param employee 员工信息
     * @return 保存后的员工
     */
    @Override
    public Employee saveEmployee(Employee employee) {
        employeeMapper.insert(employee);
        return employee;
    }

    /**
     * 更新员工
     * @param employee 员工信息
     * @return 更新后的员工
     */
    @Override
    public Employee updateEmployee(Employee employee) {
        employeeMapper.updateById(employee);
        return employee;
    }

    /**
     * 删除员工
     * @param id 员工ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteById(Long id) {
        int result = employeeMapper.deleteById(id);
        return result > 0;
    }
}
