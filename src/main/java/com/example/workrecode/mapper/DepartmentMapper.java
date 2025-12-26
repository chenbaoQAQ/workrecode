package com.example.workrecode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.workrecode.entity.Department;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门Mapper接口
 */
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 查询所有部门，并统计员工数量（employeeCount）
     * LEFT JOIN 保证没有员工的部门也能查出来
     */
    @Select(
            "SELECT " +
            " d.id, " +
            " d.name, " +
            " d.description, " +
            " COUNT(e.id) AS employee_count " +
            "FROM department d " +
            "LEFT JOIN employee e ON e.department_id = d.id " +
            "GROUP BY d.id, d.name, d.description " +
            "ORDER BY d.id DESC"
    )
    List<Department> selectAllWithEmployeeCount();

}
