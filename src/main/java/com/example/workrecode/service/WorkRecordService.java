package com.example.workrecode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.workrecode.entity.WorkRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WorkRecordService extends IService<WorkRecord> {
    List<WorkRecord> listRecords(Long employeeId, String status, String keyword, LocalDate startDate, LocalDate endDate);
    WorkRecord submit(WorkRecord record);
    WorkRecord updateRecord(WorkRecord record);
    WorkRecord approve(Long id, String adminRemark);
    WorkRecord reject(Long id, String adminRemark);
    List<Map<String, Object>> statistics(LocalDate startDate, LocalDate endDate);
}
