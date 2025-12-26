package com.example.workrecode.controller;

import com.example.workrecode.common.Result;
import com.example.workrecode.entity.Department;
import com.example.workrecode.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门Controller
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 查询所有部门
     * @return 部门列表
     */
    @GetMapping
    public Result<List<Department>> findAll() {
        List<Department> departments = departmentService.findAll();
        return Result.success(departments);
    }

    /**
     * 根据ID查询部门
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    public Result<Department> findById(@PathVariable Long id) {
        Department department = departmentService.findById(id);
        return Result.success(department);
    }

    /**
     * 保存部门
     * @param department 部门信息
     * @return 保存后的部门
     */
    @PostMapping
    public Result<Department> save(@RequestBody Department department) {
        Department savedDepartment = departmentService.saveDepartment(department);
        return Result.success("部门保存成功", savedDepartment);
    }

    /**
     * 更新部门
     * @param id 部门ID
     * @param department 部门信息
     * @return 更新后的部门
     */
    @PutMapping("/{id}")
    public Result<Department> update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        Department updatedDepartment = departmentService.updateDepartment(department);
        return Result.success("部门更新成功", updatedDepartment);
    }

    /**
     * 删除部门
     * @param id 部门ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean deleted = departmentService.deleteById(id);
        return Result.success("部门删除成功", deleted);
    }
}
