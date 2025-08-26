package com.smartoa.service;

import com.smartoa.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务接口
 */
public interface AttendanceService {
    
    /**
     * 打卡签到
     */
    AttendanceRecord checkIn(Long userId);
    
    /**
     * 打卡签退
     */
    AttendanceRecord checkOut(Long userId);
    
    /**
     * 创建考勤记录
     */
    AttendanceRecord createAttendanceRecord(AttendanceRecord record);
    
    /**
     * 更新考勤记录
     */
    AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecord record);
    
    /**
     * 删除考勤记录
     */
    void deleteAttendanceRecord(Long id);
    
    /**
     * 根据ID查询考勤记录
     */
    AttendanceRecord getAttendanceRecordById(Long id);
    
    /**
     * 根据用户ID和日期查询考勤记录
     */
    AttendanceRecord getAttendanceRecordByUserAndDate(Long userId, LocalDate date);
    
    /**
     * 分页查询用户考勤记录
     */
    Page<AttendanceRecord> getUserAttendanceRecords(Long userId, Pageable pageable);
    
    /**
     * 根据日期范围查询考勤记录
     */
    List<AttendanceRecord> getAttendanceRecordsByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 查询今日考勤记录
     */
    List<AttendanceRecord> getTodayAttendanceRecords();
    
    /**
     * 统计用户考勤数据
     */
    Map<String, Object> getAttendanceStatistics(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 统计部门考勤数据
     */
    Map<String, Object> getDepartmentAttendanceStatistics(Long deptId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 查询异常考勤记录
     */
    List<AttendanceRecord> getAbnormalAttendanceRecords(LocalDate startDate, LocalDate endDate);
    
    /**
     * 手动补签
     */
    AttendanceRecord manualSignIn(Long userId, LocalDate date, String checkInTime, String checkOutTime, String remark);
    
    /**
     * 批量导入考勤数据
     */
    List<AttendanceRecord> batchImportAttendanceRecords(List<AttendanceRecord> records);
    
    /**
     * 导出考勤数据
     */
    byte[] exportAttendanceData(Long userId, LocalDate startDate, LocalDate endDate);
} 
