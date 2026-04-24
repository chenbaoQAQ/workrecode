package com.example.workrecode.controller;

import com.example.workrecode.common.Result;
import com.example.workrecode.entity.WorkRecord;
import com.example.workrecode.service.WorkRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-records")
public class WorkRecordController {

    @Autowired
    private WorkRecordService workRecordService;

    @GetMapping
    public Result<List<WorkRecord>> list(@RequestParam(required = false) Long employeeId,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(workRecordService.listRecords(employeeId, status, keyword, startDate, endDate));
    }

    @PostMapping
    public Result<WorkRecord> submit(@RequestBody WorkRecord record) {
        if (record.getWorkDate() != null && record.getWorkDate().isBefore(LocalDate.now().minusDays(7))) {
            return Result.error(400, "只能补登最近7天内的工时");
        }
        return Result.success("工时提交成功", workRecordService.submit(record));
    }

    @PutMapping("/{id}")
    public Result<WorkRecord> update(@PathVariable Long id, @RequestBody WorkRecord record) {
        record.setId(id);
        return Result.success("工时记录更新成功", workRecordService.updateRecord(record));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success("工时记录删除成功", workRecordService.removeById(id));
    }

    @PostMapping("/{id}/approve")
    public Result<WorkRecord> approve(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String remark = body == null ? "" : body.getOrDefault("adminRemark", "");
        return Result.success("审批通过", workRecordService.approve(id, remark));
    }

    @PostMapping("/{id}/reject")
    public Result<WorkRecord> reject(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String remark = body == null ? "" : body.getOrDefault("adminRemark", "");
        return Result.success("已驳回申请", workRecordService.reject(id, remark));
    }

    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> statistics(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(workRecordService.statistics(startDate, endDate));
    }
}
