package com.example.workrecode.controller;

import com.example.workrecode.common.Result;
import com.example.workrecode.entity.Employee;
import com.example.workrecode.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工Controller
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 查询所有员工，并关联部门信息
     * @return 员工列表
     */
    @GetMapping
    public Result<List<Employee>> findAll() {
        List<Employee> employees = employeeService.findAllWithDepartment();
        return Result.success(employees);
    }

    /**
     * 根据ID查询员工
     * @param id 员工ID
     * @return 员工信息
     */
    @GetMapping("/{id}")
    public Result<Employee> findById(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        return Result.success(employee);
    }

    /**
     * 根据部门ID查询员工
     * @param departmentId 部门ID
     * @return 员工列表
     */
    @GetMapping("/department/{departmentId}")
    public Result<List<Employee>> findByDepartmentId(@PathVariable Long departmentId) {
        List<Employee> employees = employeeService.findByDepartmentId(departmentId);
        return Result.success(employees);
    }

    /**
     * 保存员工
     * @param employee 员工信息
     * @return 保存后的员工
     */
    @PostMapping
    public Result<Employee> save(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return Result.success("员工保存成功", savedEmployee);
    }

    /**
     * 更新员工
     * @param id 员工ID
     * @param employee 员工信息
     * @return 更新后的员工
     */
    @PutMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        return Result.success("员工更新成功", updatedEmployee);
    }

    /**
     * 删除员工
     * @param id 员工ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean deleted = employeeService.deleteById(id);
        return Result.success("员工删除成功", deleted);
    }
}
