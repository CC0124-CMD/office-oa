package com.smartoa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 考勤记录实体�?
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "att_attendance_record")
public class AttendanceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @Column(name = "check_in_time")
    private LocalTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalTime checkOutTime;
    
    @Column(name = "work_hours")
    private Double workHours;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status = Status.NORMAL;
    
    @Column(name = "remark", length = 500)
    private String remark;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    public enum Status {
        NORMAL(0, "正常"),
        LATE(1, "迟到"),
        EARLY_LEAVE(2, "早退"),
        ABSENT(3, "缺勤"),
        OVERTIME(4, "加班");
        
        private final int code;
        private final String description;
        
        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 
