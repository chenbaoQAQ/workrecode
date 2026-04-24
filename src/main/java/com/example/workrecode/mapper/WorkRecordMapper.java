package com.example.workrecode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.workrecode.entity.WorkRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface WorkRecordMapper extends BaseMapper<WorkRecord> {
    List<WorkRecord> listWithProject(@Param("employeeId") Long employeeId,
                                     @Param("status") String status,
                                     @Param("keyword") String keyword,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> statistics(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    java.math.BigDecimal sumApprovedWeightByEmployeeAndDate(@Param("employeeId") Long employeeId,
                                                            @Param("workDate") LocalDate workDate,
                                                            @Param("excludeId") Long excludeId);

    List<WorkRecord> selectByEmployeeAndDate(@Param("employeeId") Long employeeId,
                                             @Param("workDate") LocalDate workDate);
}
