package com.example.workrecode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("work_record")
public class WorkRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String workContent;
    private Long employeeId;
    private String employeeName;
    private LocalDate workDate;
    private BigDecimal workHours;
    private BigDecimal statHours;
    private BigDecimal overtimeHours;
    private String status;
    private String remark;
    private String adminRemark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String projectName;
}
