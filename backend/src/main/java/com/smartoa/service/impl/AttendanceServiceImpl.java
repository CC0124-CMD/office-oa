package com.smartoa.service.impl;

import com.smartoa.entity.AttendanceRecord;
import com.smartoa.repository.AttendanceRecordRepository;
import com.smartoa.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考勤服务实现�?
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRecordRepository attendanceRecordRepository;
    
    // 标准工作时间配置
    private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(9, 0);
    private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(18, 0);
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 30);
    private static final LocalTime EARLY_LEAVE_THRESHOLD = LocalTime.of(17, 30);
    
    @Override
    public AttendanceRecord checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        // 检查是否已经打�?
        Optional<AttendanceRecord> existingRecord = attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, today);
        if (existingRecord.isPresent() && existingRecord.get().getCheckInTime() != null) {
            throw new RuntimeException("今日已签到，请勿重复打卡");
        }
        
        AttendanceRecord record;
        if (existingRecord.isPresent()) {
            record = existingRecord.get();
        } else {
            record = new AttendanceRecord();
            record.setUserId(userId);
            record.setAttendanceDate(today);
        }
        
        record.setCheckInTime(now);
        
        // 判断考勤状�?
        if (now.isAfter(LATE_THRESHOLD)) {
            record.setStatus(AttendanceRecord.Status.LATE);
        } else {
            record.setStatus(AttendanceRecord.Status.NORMAL);
        }
        
        return attendanceRecordRepository.save(record);
    }
    
    @Override
    public AttendanceRecord checkOut(Long userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        AttendanceRecord record = attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, today)
                .orElseThrow(() -> new RuntimeException("请先签到"));
        
        if (record.getCheckOutTime() != null) {
            throw new RuntimeException("今日已签退，请勿重复打�?);
        }
        
        record.setCheckOutTime(now);
        
        // 计算工作时长
        if (record.getCheckInTime() != null) {
            double workHours = java.time.Duration.between(record.getCheckInTime(), now).toHours();
            record.setWorkHours(workHours);
        }
        
        // 判断是否早退
        if (now.isBefore(EARLY_LEAVE_THRESHOLD)) {
            record.setStatus(AttendanceRecord.Status.EARLY_LEAVE);
        } else if (record.getStatus() == AttendanceRecord.Status.LATE) {
            // 如果迟到但按时下班，状态保持为迟到
            record.setStatus(AttendanceRecord.Status.LATE);
        } else {
            record.setStatus(AttendanceRecord.Status.NORMAL);
        }
        
        return attendanceRecordRepository.save(record);
    }
    
    @Override
    public AttendanceRecord createAttendanceRecord(AttendanceRecord record) {
        // 检查是否已存在该日期的记录
        Optional<AttendanceRecord> existing = attendanceRecordRepository.findByUserIdAndAttendanceDate(
                record.getUserId(), record.getAttendanceDate());
        if (existing.isPresent()) {
            throw new RuntimeException("该日期的考勤记录已存�?);
        }
        
        return attendanceRecordRepository.save(record);
    }
    
    @Override
    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecord record) {
        AttendanceRecord existing = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("考勤记录不存�?));
        
        existing.setCheckInTime(record.getCheckInTime());
        existing.setCheckOutTime(record.getCheckOutTime());
        existing.setWorkHours(record.getWorkHours());
        existing.setStatus(record.getStatus());
        existing.setRemark(record.getRemark());
        
        return attendanceRecordRepository.save(existing);
    }
    
    @Override
    public void deleteAttendanceRecord(Long id) {
        attendanceRecordRepository.deleteById(id);
    }
    
    @Override
    public AttendanceRecord getAttendanceRecordById(Long id) {
        return attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("考勤记录不存�?));
    }
    
    @Override
    public AttendanceRecord getAttendanceRecordByUserAndDate(Long userId, LocalDate date) {
        return attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, date)
                .orElse(null);
    }
    
    @Override
    public Page<AttendanceRecord> getUserAttendanceRecords(Long userId, Pageable pageable) {
        return attendanceRecordRepository.findByUserIdOrderByAttendanceDateDesc(userId, pageable);
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceRecordsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(userId, startDate, endDate);
    }
    
    @Override
    public List<AttendanceRecord> getTodayAttendanceRecords() {
        return attendanceRecordRepository.findByAttendanceDate(LocalDate.now());
    }
    
    @Override
    public Map<String, Object> getAttendanceStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总出勤天�?
        long totalDays = attendanceRecordRepository.countByUserIdAndDateRange(userId, startDate, endDate);
        
        // 正常出勤天数
        long normalDays = attendanceRecordRepository.countNormalAttendanceByUserIdAndDateRange(userId, startDate, endDate);
        
        // 迟到次数
        List<AttendanceRecord> lateRecords = attendanceRecordRepository.findByStatusAndAttendanceDateBetween(
                AttendanceRecord.Status.LATE, startDate, endDate);
        
        // 早退次数
        List<AttendanceRecord> earlyLeaveRecords = attendanceRecordRepository.findByStatusAndAttendanceDateBetween(
                AttendanceRecord.Status.EARLY_LEAVE, startDate, endDate);
        
        // 缺勤天数
        long absentDays = totalDays - normalDays - lateRecords.size() - earlyLeaveRecords.size();
        
        // 总工作时�?
        Double totalWorkHours = attendanceRecordRepository.sumWorkHoursByUserIdAndDateRange(userId, startDate, endDate);
        
        statistics.put("totalDays", totalDays);
        statistics.put("normalDays", normalDays);
        statistics.put("lateCount", lateRecords.size());
        statistics.put("earlyLeaveCount", earlyLeaveRecords.size());
        statistics.put("absentDays", absentDays);
        statistics.put("totalWorkHours", totalWorkHours != null ? totalWorkHours : 0.0);
        statistics.put("attendanceRate", totalDays > 0 ? (double) normalDays / totalDays * 100 : 0.0);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getDepartmentAttendanceStatistics(Long deptId, LocalDate startDate, LocalDate endDate) {
        // 这里需要根据部门ID查询该部门下所有用户的考勤统计
        // 暂时返回空Map，后续可以扩�?
        return new HashMap<>();
    }
    
    @Override
    public List<AttendanceRecord> getAbnormalAttendanceRecords(LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> abnormalRecords = new java.util.ArrayList<>();
        
        // 查询迟到记录
        abnormalRecords.addAll(attendanceRecordRepository.findByStatusAndAttendanceDateBetween(
                AttendanceRecord.Status.LATE, startDate, endDate));
        
        // 查询早退记录
        abnormalRecords.addAll(attendanceRecordRepository.findByStatusAndAttendanceDateBetween(
                AttendanceRecord.Status.EARLY_LEAVE, startDate, endDate));
        
        // 查询缺勤记录
        abnormalRecords.addAll(attendanceRecordRepository.findByStatusAndAttendanceDateBetween(
                AttendanceRecord.Status.ABSENT, startDate, endDate));
        
        return abnormalRecords;
    }
    
    @Override
    public AttendanceRecord manualSignIn(Long userId, LocalDate date, String checkInTime, String checkOutTime, String remark) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        AttendanceRecord record = new AttendanceRecord();
        record.setUserId(userId);
        record.setAttendanceDate(date);
        record.setRemark(remark);
        
        if (checkInTime != null && !checkInTime.isEmpty()) {
            record.setCheckInTime(LocalTime.parse(checkInTime, formatter));
        }
        
        if (checkOutTime != null && !checkOutTime.isEmpty()) {
            record.setCheckOutTime(LocalTime.parse(checkOutTime, formatter));
        }
        
        // 计算工作时长
        if (record.getCheckInTime() != null && record.getCheckOutTime() != null) {
            double workHours = java.time.Duration.between(record.getCheckInTime(), record.getCheckOutTime()).toHours();
            record.setWorkHours(workHours);
        }
        
        // 判断考勤状�?
        if (record.getCheckInTime() != null && record.getCheckInTime().isAfter(LATE_THRESHOLD)) {
            record.setStatus(AttendanceRecord.Status.LATE);
        } else if (record.getCheckOutTime() != null && record.getCheckOutTime().isBefore(EARLY_LEAVE_THRESHOLD)) {
            record.setStatus(AttendanceRecord.Status.EARLY_LEAVE);
        } else {
            record.setStatus(AttendanceRecord.Status.NORMAL);
        }
        
        return attendanceRecordRepository.save(record);
    }
    
    @Override
    public List<AttendanceRecord> batchImportAttendanceRecords(List<AttendanceRecord> records) {
        return attendanceRecordRepository.saveAll(records);
    }
    
    @Override
    public byte[] exportAttendanceData(Long userId, LocalDate startDate, LocalDate endDate) {
        // 导出功能暂未实现，返回空数组
        return new byte[0];
    }
} 
