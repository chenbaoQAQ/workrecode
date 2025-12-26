package com.example.workrecode.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.workrecode.entity.Department;
import com.example.workrecode.entity.Employee;
import com.example.workrecode.mapper.DepartmentMapper;
import com.example.workrecode.mapper.EmployeeMapper;
import com.example.workrecode.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门Service实现类
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public List<Department> findAll() {
        // 带 employeeCount 的列表
        return departmentMapper.selectAllWithEmployeeCount();
    }

    @Override
    public Department findById(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public Department saveDepartment(Department department) {
        departmentMapper.insert(department);
        return department;
    }

    @Override
    public Department updateDepartment(Department department) {
        departmentMapper.updateById(department);
        return department;
    }

    @Override
    public boolean deleteById(Long id) {
        // 你的需求：删部门后，员工的 department_id 置 NULL
        employeeMapper.update(
                null,
                new UpdateWrapper<Employee>()
                        .set("department_id", null)
                        .eq("department_id", id)
        );

        int result = departmentMapper.deleteById(id);
        return result > 0;
    }
}
