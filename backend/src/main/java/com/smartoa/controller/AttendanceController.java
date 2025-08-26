package com.smartoa.controller;

import com.smartoa.common.Result;
import com.smartoa.entity.AttendanceRecord;
import com.smartoa.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤管理控制�?
 */
@Slf4j
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @PostMapping("/check-in")
    public Result<AttendanceRecord> checkIn() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            AttendanceRecord record = attendanceService.checkIn(userId);
            return Result.success("签到成功", record);
        } catch (Exception e) {
            log.error("签到失败", e);
            return Result.error("签到失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/check-out")
    public Result<AttendanceRecord> checkOut() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            AttendanceRecord record = attendanceService.checkOut(userId);
            return Result.success("签退成功", record);
        } catch (Exception e) {
            log.error("签退失败", e);
            return Result.error("签退失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/today")
    public Result<AttendanceRecord> getTodayRecord() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            AttendanceRecord record = attendanceService.getAttendanceRecordByUserAndDate(userId, LocalDate.now());
            return Result.success(record);
        } catch (Exception e) {
            log.error("获取今日考勤记录失败", e);
            return Result.error("获取今日考勤记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/my")
    public Result<Page<AttendanceRecord>> getMyAttendanceRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            Pageable pageable = PageRequest.of(page, size);
            Page<AttendanceRecord> records = attendanceService.getUserAttendanceRecords(userId, pageable);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取我的考勤记录失败", e);
            return Result.error("获取我的考勤记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getAttendanceStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            Map<String, Object> statistics = attendanceService.getAttendanceStatistics(userId, start, end);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取考勤统计失败", e);
            return Result.error("获取考勤统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/range")
    public Result<List<AttendanceRecord>> getAttendanceRecordsByRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<AttendanceRecord> records = attendanceService.getAttendanceRecordsByDateRange(userId, start, end);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取考勤记录失败", e);
            return Result.error("获取考勤记录失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/manual")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AttendanceRecord> manualSignIn(
            @RequestParam Long userId,
            @RequestParam String date,
            @RequestParam(required = false) String checkInTime,
            @RequestParam(required = false) String checkOutTime,
            @RequestParam(required = false) String remark) {
        try {
            LocalDate attendanceDate = LocalDate.parse(date);
            AttendanceRecord record = attendanceService.manualSignIn(userId, attendanceDate, checkInTime, checkOutTime, remark);
            return Result.success("补签成功", record);
        } catch (Exception e) {
            log.error("补签失败", e);
            return Result.error("补签失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/today/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AttendanceRecord>> getTodayAllRecords() {
        try {
            List<AttendanceRecord> records = attendanceService.getTodayAttendanceRecords();
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取今日所有考勤记录失败", e);
            return Result.error("获取今日所有考勤记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/abnormal")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AttendanceRecord>> getAbnormalRecords(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<AttendanceRecord> records = attendanceService.getAbnormalAttendanceRecords(start, end);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取异常考勤记录失败", e);
            return Result.error("获取异常考勤记录失败: " + e.getMessage());
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AttendanceRecord> createAttendanceRecord(@Validated @RequestBody AttendanceRecord record) {
        try {
            AttendanceRecord created = attendanceService.createAttendanceRecord(record);
            return Result.success("考勤记录创建成功", created);
        } catch (Exception e) {
            log.error("创建考勤记录失败", e);
            return Result.error("创建考勤记录失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AttendanceRecord> updateAttendanceRecord(
            @PathVariable Long id,
            @Validated @RequestBody AttendanceRecord record) {
        try {
            AttendanceRecord updated = attendanceService.updateAttendanceRecord(id, record);
            return Result.success("考勤记录更新成功", updated);
        } catch (Exception e) {
            log.error("更新考勤记录失败", e);
            return Result.error("更新考勤记录失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteAttendanceRecord(@PathVariable Long id) {
        try {
            attendanceService.deleteAttendanceRecord(id);
            return Result.success("考勤记录删除成功", null);
        } catch (Exception e) {
            log.error("删除考勤记录失败", e);
            return Result.error("删除考勤记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<AttendanceRecord> getAttendanceRecord(@PathVariable Long id) {
        try {
            AttendanceRecord record = attendanceService.getAttendanceRecordById(id);
            return Result.success(record);
        } catch (Exception e) {
            log.error("获取考勤记录失败", e);
            return Result.error("获取考勤记录失败: " + e.getMessage());
        }
    }
} 
