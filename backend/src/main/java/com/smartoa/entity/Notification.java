package com.smartoa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 通知实体�?
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_notification")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private Type type = Type.SYSTEM;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    public enum Type {
        SYSTEM(1, "系统通知"),
        APPROVAL(2, "审批通知"),
        ATTENDANCE(3, "考勤通知"),
        ANNOUNCEMENT(4, "公告通知");
        
        private final int code;
        private final String description;
        
        Type(int code, String description) {
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
