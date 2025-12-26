package com.example.workrecode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.workrecode.entity.Department;

import java.util.List;

/**
 * 部门Service接口
 */
public interface DepartmentService extends IService<Department> {
    // 继承IService后，自动获得基本的CRUD方法

    /**
     * 查询所有部门
     * @return 部门列表
     */
    List<Department> findAll();

    /**
     * 根据ID查询部门
     * @param id 部门ID
     * @return 部门信息
     */
    Department findById(Long id);

    /**
     * 保存部门
     * @param department 部门信息
     * @return 保存后的部门
     */
    Department saveDepartment(Department department);

    /**
     * 更新部门
     * @param department 部门信息
     * @return 更新后的部门
     */
    Department updateDepartment(Department department);

    /**
     * 删除部门
     * @param id 部门ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
}
