package com.example.workrecode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.workrecode.entity.WorkProject;

import java.util.List;

public interface WorkProjectService extends IService<WorkProject> {
    List<WorkProject> findAll(String keyword);
    WorkProject saveProject(WorkProject project);
    WorkProject updateProject(WorkProject project);
    boolean deleteProject(Long id);
}
