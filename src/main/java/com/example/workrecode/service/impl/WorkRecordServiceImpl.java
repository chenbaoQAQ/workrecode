package com.example.workrecode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.workrecode.entity.WorkRecord;
import com.example.workrecode.mapper.WorkRecordMapper;
import com.example.workrecode.service.WorkProjectService;
import com.example.workrecode.service.WorkRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WorkRecordServiceImpl extends ServiceImpl<WorkRecordMapper, WorkRecord> implements WorkRecordService {

    private final WorkProjectService workProjectService;

    public WorkRecordServiceImpl(WorkProjectService workProjectService) {
        this.workProjectService = workProjectService;
    }

    @Override
    public List<WorkRecord> listRecords(Long employeeId, String status, String keyword, LocalDate startDate, LocalDate endDate) {
        return baseMapper.listWithProject(employeeId, status, keyword, startDate, endDate);
    }

    @Override
    public WorkRecord submit(WorkRecord record) {
        fillBaseFields(record);
        save(record);
        recalculateDay(record.getEmployeeId(), record.getWorkDate(), record.getId(), false);
        return getById(record.getId());
    }

    @Override
    public WorkRecord updateRecord(WorkRecord record) {
        WorkRecord existing = getById(record.getId());
        if (existing == null) {
            return null;
        }
        existing.setStatus(record.getStatus());
        existing.setAdminRemark(record.getAdminRemark());
        updateById(existing);
        recalculateDay(existing.getEmployeeId(), existing.getWorkDate(), null, false);
        return getById(record.getId());
    }

    @Override
    public WorkRecord approve(Long id, String adminRemark) {
        WorkRecord record = getById(id);
        record.setStatus("APPROVED");
        record.setAdminRemark(adminRemark);
        updateById(record);
        recalculateDay(record.getEmployeeId(), record.getWorkDate(), null, false);
        return getById(id);
    }

    @Override
    public WorkRecord reject(Long id, String adminRemark) {
        WorkRecord record = getById(id);
        record.setWorkHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        record.setStatHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        record.setOvertimeHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        record.setStatus("REJECTED");
        record.setAdminRemark(adminRemark);
        updateById(record);
        recalculateDay(record.getEmployeeId(), record.getWorkDate(), null, false);
        return getById(id);
    }

    @Override
    public WorkRecord cancel(Long id, Long employeeId) {
        WorkRecord record = getById(id);
        if (record == null) {
            throw new IllegalArgumentException("填报记录不存在");
        }
        if (employeeId != null && record.getEmployeeId() != null && !employeeId.equals(record.getEmployeeId())) {
            throw new IllegalArgumentException("只能撤回自己的填报记录");
        }
        if (!"PENDING".equals(record.getStatus())) {
            throw new IllegalStateException("只有待审批记录可以撤回");
        }

        record.setStatHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        record.setOvertimeHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        record.setStatus("CANCELLED");
        record.setAdminRemark("员工已撤回");
        updateById(record);
        recalculateDay(record.getEmployeeId(), record.getWorkDate(), null, false);
        return getById(id);
    }

    @Override
    public List<Map<String, Object>> statistics(LocalDate startDate, LocalDate endDate) {
        return baseMapper.statistics(startDate, endDate);
    }

    private void fillBaseFields(WorkRecord record) {
        if (record.getProjectId() != null) {
            var project = workProjectService.getById(record.getProjectId());
            if (project != null && StringUtils.hasText(project.getName())) {
                record.setWorkContent(project.getName().trim());
            }
        }
        if (record.getWorkHours() == null) {
            record.setWorkHours(BigDecimal.ZERO);
        }
        record.setWorkHours(record.getWorkHours().setScale(2, RoundingMode.HALF_UP));
        record.setStatHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        record.setOvertimeHours(BigDecimal.ZERO);
        if (record.getStatus() == null) {
            record.setStatus("PENDING");
        }
    }

    private void recalculateDay(Long employeeId, LocalDate workDate, Long submittedRecordId, boolean markSubmittedPending) {
        List<WorkRecord> records = baseMapper.selectByEmployeeAndDate(employeeId, workDate);
        if (records.isEmpty()) {
            return;
        }
        BigDecimal usedNormalHours = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (WorkRecord record : records) {
            if ("REJECTED".equals(record.getStatus()) || "CANCELLED".equals(record.getStatus())) {
                record.setStatHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                record.setOvertimeHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                updateById(record);
                continue;
            }
            if (!"APPROVED".equals(record.getStatus())) {
                record.setStatHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                record.setOvertimeHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                updateById(record);
                continue;
            }

            BigDecimal weight = record.getWorkHours() == null ? BigDecimal.ZERO : record.getWorkHours();
            BigDecimal remainingNormal = BigDecimal.ONE.subtract(usedNormalHours).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal normalWeight = weight.min(remainingNormal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal overtimeWeight = weight.subtract(normalWeight).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            boolean isSubmittedRecord = submittedRecordId != null && submittedRecordId.equals(record.getId());
            if (markSubmittedPending && isSubmittedRecord && overtimeWeight.compareTo(BigDecimal.ZERO) > 0) {
                record.setStatus("PENDING");
                record.setStatHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                record.setOvertimeHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                updateById(record);
                continue;
            }

            record.setStatHours(normalWeight);
            record.setOvertimeHours(overtimeWeight);
            usedNormalHours = usedNormalHours.add(normalWeight).setScale(2, RoundingMode.HALF_UP);
            updateById(record);
        }
    }
}
