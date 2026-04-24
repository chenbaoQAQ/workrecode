package com.example.workrecode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.workrecode.entity.WorkProject;
import com.example.workrecode.mapper.WorkProjectMapper;
import com.example.workrecode.service.WorkProjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WorkProjectServiceImpl extends ServiceImpl<WorkProjectMapper, WorkProject> implements WorkProjectService {

    @Override
    public List<WorkProject> findAll(String keyword) {
        QueryWrapper<WorkProject> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("name", keyword.trim());
        }
        wrapper.orderByDesc("id");
        return list(wrapper);
    }

    @Override
    public WorkProject saveProject(WorkProject project) {
        if (project.getEnabled() == null) {
            project.setEnabled(true);
        }
        save(project);
        return project;
    }

    @Override
    public WorkProject updateProject(WorkProject project) {
        updateById(project);
        return project;
    }

    @Override
    public boolean deleteProject(Long id) {
        return removeById(id);
    }
}
