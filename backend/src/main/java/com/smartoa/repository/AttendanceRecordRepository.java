package com.smartoa.repository;

import com.smartoa.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考勤记录Repository
 */
@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    
    /**
     * 根据用户ID和日期查询考勤记录
     */
    Optional<AttendanceRecord> findByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate);
    
    /**
     * 根据用户ID查询考勤记录
     */
    Page<AttendanceRecord> findByUserIdOrderByAttendanceDateDesc(Long userId, Pageable pageable);
    
    /**
     * 根据日期范围查询考勤记录
     */
    List<AttendanceRecord> findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据状态查询考勤记录
     */
    Page<AttendanceRecord> findByStatusOrderByAttendanceDateDesc(AttendanceRecord.Status status, Pageable pageable);
    
    /**
     * 根据用户ID和状态查询考勤记录
     */
    List<AttendanceRecord> findByUserIdAndStatus(Long userId, AttendanceRecord.Status status);
    
    /**
     * 统计用户某月的考勤记录
     */
    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    long countByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 统计用户某月的正常出勤天�?
     */
    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.status = 0")
    long countNormalAttendanceByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 查询今日考勤记录
     */
    List<AttendanceRecord> findByAttendanceDate(LocalDate attendanceDate);
    
    /**
     * 查询迟到记录
     */
    List<AttendanceRecord> findByStatusAndAttendanceDateBetween(AttendanceRecord.Status status, LocalDate startDate, LocalDate endDate);
    
    /**
     * 统计用户总工作时�?
     */
    @Query("SELECT SUM(a.workHours) FROM AttendanceRecord a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Double sumWorkHoursByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
} 
