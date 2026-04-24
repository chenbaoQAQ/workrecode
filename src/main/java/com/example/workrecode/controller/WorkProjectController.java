package com.example.workrecode.controller;

import com.example.workrecode.common.Result;
import com.example.workrecode.entity.WorkProject;
import com.example.workrecode.service.WorkProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-projects")
public class WorkProjectController {

    @Autowired
    private WorkProjectService workProjectService;

    @GetMapping
    public Result<List<WorkProject>> findAll(@RequestParam(required = false) String keyword) {
        return Result.success(workProjectService.findAll(keyword));
    }

    @PostMapping
    public Result<WorkProject> save(@RequestBody WorkProject project) {
        return Result.success("项目保存成功", workProjectService.saveProject(project));
    }

    @PutMapping("/{id}")
    public Result<WorkProject> update(@PathVariable Long id, @RequestBody WorkProject project) {
        project.setId(id);
        return Result.success("项目更新成功", workProjectService.updateProject(project));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success("项目删除成功", workProjectService.deleteProject(id));
    }
}
